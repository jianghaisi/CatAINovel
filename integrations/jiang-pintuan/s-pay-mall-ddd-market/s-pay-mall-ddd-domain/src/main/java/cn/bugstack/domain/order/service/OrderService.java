package cn.bugstack.domain.order.service;

import cn.bugstack.domain.order.adapter.port.IProductPort;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.MarketPayDiscountEntity;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OrderService extends AbstractOrderService {

    @Value("${alipay.notify_url}")
    private String notifyUrl;
    @Value("${alipay.return_url}")
    private String returnUrl;

    @Resource
    private AlipayClient alipayClient;

    public OrderService(IOrderRepository repository, IProductPort port) {
        super(repository, port);
    }

    @Override
    protected void doSaveOrder(CreateOrderAggregate orderAggregate) {
        repository.doSaveOrder(orderAggregate);
    }

    @Override
    protected MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId) {
        // 閫氳繃鍩虹璁炬柦灞傜殑 ProductPort 璋冪敤 group-buy-market 鐨勯攣鍗曟帴鍙ｃ€?        return port.lockMarketPayOrder(userId, teamId, activityId, productId, orderId);
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException {
        return doPrepayOrder(userId, productId, productName, orderId, totalAmount, null);
    }

    @Override
    protected PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException {
        // 濡傛灉鏈夋嫾鍥紭鎯狅紝灏辩敤鎷煎洟杩斿洖鐨?payPrice锛涘惁鍒欎娇鐢ㄥ晢鍝佸師浠枫€?        BigDecimal payAmount = null == marketPayDiscountEntity ? totalAmount : marketPayDiscountEntity.getPayPrice();

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notifyUrl);
        request.setReturnUrl(returnUrl);

        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", payAmount);
        bizContent.put("subject", productName);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        // pageExecute 杩斿洖鐨勬槸涓€娈垫敮浠樺疂鏀粯琛ㄥ崟 HTML锛屽墠绔嬁鍒板悗鍗冲彲璺宠浆/娓叉煋鏀粯椤点€?        String form = alipayClient.pageExecute(request).getBody();

        PayOrderEntity payOrderEntity = new PayOrderEntity();
        payOrderEntity.setOrderId(orderId);
        payOrderEntity.setPayUrl(form);
        payOrderEntity.setOrderStatus(OrderStatusVO.PAY_WAIT);

        // 鎶婃敮浠橀摼鎺ャ€佹敮浠橀噾棰濄€佽惀閿€鎶垫墸閲戦鍥炲啓鍒版湰鍦拌鍗曡〃锛屽悗缁洖璋冨拰鏌ヨ閮戒緷璧栬繖浜涘瓧娈点€?        payOrderEntity.setMarketType(null == marketPayDiscountEntity ? MarketTypeVO.NO_MARKET.getCode() : MarketTypeVO.GROUP_BUY_MARKET.getCode());
        payOrderEntity.setMarketDeductionAmount(null == marketPayDiscountEntity ? BigDecimal.ZERO : marketPayDiscountEntity.getDeductionPrice());
        payOrderEntity.setPayAmount(payAmount);

        repository.updateOrderPayInfo(payOrderEntity);

        return payOrderEntity;
    }

    @Override
    public void changeOrderPaySuccess(String orderId, Date payTime) {
        OrderEntity orderEntity = repository.queryOrderByOrderId(orderId);
        if (null == orderEntity) return;

        if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(orderEntity.getMarketType())) {
            // 鎷煎洟璁㈠崟鏀粯鎴愬姛鍚庯紝闇€瑕佸厛鏇存柊鍟嗗煄璁㈠崟锛屽啀閫氱煡鎷煎洟鏈嶅姟鍋氭垚鍥㈢粨绠椼€?            repository.changeMarketOrderPaySuccess(orderId);
            // 鍙戣捣钀ラ攢缁撶畻銆傝繖涓繃绋嬪彲浠ユ槸http/rpc鐩存帴璋冪敤锛屼篃鍙互鍙戜竴涓晢鍩庝氦鏄撴敮浠樺畬鎴愮殑娑堟伅锛屼箣鍚庢嫾鍥㈢郴缁熻嚜宸辨帴鏀跺仛缁撶畻銆?
            port.settlementMarketPayOrder(orderEntity.getUserId(), orderId, payTime);
            // 娉ㄦ剰锛涘湪鍏徃涓紝鍙戣捣缁撶畻鐨刪ttp/rpc璋冪敤鍙兘浼氬け璐ワ紝杩欎釜鏃跺€欒繕浼氭湁澧炲姞job浠诲姟琛ュ伩銆傛潯浠朵负锛屾鏌ヤ竴绗旇蛋浜嗘嫾鍥㈢殑璁㈠崟锛岃秴杩噉鍒嗛挓鍚庯紝浠嶇劧娌℃湁鍋氭嫾鍥㈢粨绠楃姸鎬佸彉鏇淬€?
            // 鎴戜滑杩欓噷澶辫触浜嗭紝浼氭姏寮傚父锛屽€熷姪鏀粯瀹濆洖璋?job鏉ラ噸璇曘€備綘鍙互鍗曠嫭瀹炵幇涓€涓嫭绔嬬殑job鏉ュ鐞嗐€?
        } else {
            // 鏅€氳鍗曚笉闇€瑕佹嫾鍥㈢粨绠楋紝鐩存帴鏀逛负鏀粯鎴愬姛骞跺彂甯冩敮浠樻垚鍔熸秷鎭€?            repository.changeOrderPaySuccess(orderId, payTime);
        }

    }

    @Override
    public List<String> queryNoPayNotifyOrder() {
        return repository.queryNoPayNotifyOrder();
    }

    @Override
    public List<String> queryTimeoutCloseOrderList() {
        return repository.queryTimeoutCloseOrderList();
    }

    @Override
    public boolean changeOrderClose(String orderId) {
        return repository.changeOrderClose(orderId);
    }

    @Override
    public void changeOrderMarketSettlement(List<String> outTradeNoList) {
        // 鎷煎洟鎴愬洟鍥炶皟鍚庯紝鍟嗗煄鎶婅繖涓€鎵硅鍗曟洿鏂颁负鏈€缁堝彲灞ョ害鐘舵€併€?        repository.changeOrderMarketSettlement(outTradeNoList);
    }

    @Override
    public boolean refundMarketOrder(String userId, String orderId) {
        // 1. 鍏堥獙璇佽鍗曞瓨鍦ㄤ笖灞炰簬褰撳墠鐢ㄦ埛锛岄槻姝㈣秺鏉冮€€鍗曘€?        OrderEntity orderEntity = repository.queryOrderByUserIdAndOrderId(userId, orderId);
        if (null == orderEntity) {
            log.warn("閫€鍗曞け璐ワ紝璁㈠崟涓嶅瓨鍦ㄦ垨涓嶅睘浜庤鐢ㄦ埛 userId:{} orderId:{}", userId, orderId);
            return false;
        }

        // 2. 宸插叧闂鍗曚笉鑳介噸澶嶉€€鍗曘€?        String status = orderEntity.getOrderStatusVO().getCode();
        if (OrderStatusVO.CLOSE.getCode().equals(status)) {
            log.warn("閫€鍗曞け璐ワ紝璁㈠崟宸插叧闂?userId:{} orderId:{} status:{}", userId, orderId, status);
            return false;
        }

        // 3. 钀ラ攢渚у厛鍋氶€嗗悜澶勭悊锛氶噴鏀惧悕棰濄€佸洖婊氶槦浼嶇姸鎬併€佸啓閫氱煡浠诲姟绛夈€?        port.refundMarketPayOrder(userId, orderId);

        // 4. 鏈湴璁㈠崟鐘舵€佸洖婊氾紱鏈敮浠樿鍗曟棤闇€鐪熷疄閫€娆撅紝宸叉敮浠樿鍗曞簲鍐嶈蛋鏀粯娓犻亾閫€娆俱€?        if (OrderStatusVO.CREATE.getCode().equals(status) || OrderStatusVO.PAY_WAIT.getCode().equals(status)) {
            return repository.refundOrder(userId, orderId);
        } else {
            boolean result = repository.refundMarketOrder(userId, orderId);
            if (result) {
                log.info("閫€鍗曟垚鍔?userId:{} orderId:{}", userId, orderId);
            } else {
                log.warn("閫€鍗曞け璐?userId:{} orderId:{}", userId, orderId);
            }
            return result;
        }

    }

    @Override
    public boolean refundPayOrder(String userId, String orderId) throws AlipayApiException {
        // 1. 鏌ヨ璁㈠崟淇℃伅锛岄獙璇佽鍗曟槸鍚﹀瓨鍦ㄤ笖灞炰簬璇ョ敤鎴?
        OrderEntity orderEntity = repository.queryOrderByUserIdAndOrderId(userId, orderId);
        if (null == orderEntity) {
            log.warn("閫€娆惧け璐ワ紝璁㈠崟涓嶅瓨鍦ㄦ垨涓嶅睘浜庤鐢ㄦ埛 userId:{} orderId:{}", userId, orderId);
            return false;
        }

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel refundModel = new AlipayTradeRefundModel();
        refundModel.setOutTradeNo(orderEntity.getOrderId());
        refundModel.setRefundAmount(orderEntity.getPayAmount().toString());
        refundModel.setRefundReason("浜ゆ槗閫€鍗?);
        request.setBizModel(refundModel);

        // 浜ゆ槗閫€娆?
        AlipayTradeRefundResponse execute = alipayClient.execute(request);
        if (!execute.isSuccess()) return false;

        // 鐘舵€佸彉鏇?
        repository.refundOrder(userId, orderId);

        return true;
    }

}
