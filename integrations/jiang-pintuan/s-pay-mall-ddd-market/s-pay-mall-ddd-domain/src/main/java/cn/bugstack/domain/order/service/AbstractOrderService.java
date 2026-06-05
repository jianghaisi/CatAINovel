package cn.bugstack.domain.order.service;

import cn.bugstack.domain.order.adapter.port.IProductPort;
import cn.bugstack.domain.order.adapter.repository.IOrderRepository;
import cn.bugstack.domain.order.model.aggregate.CreateOrderAggregate;
import cn.bugstack.domain.order.model.entity.*;
import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
public abstract class AbstractOrderService implements IOrderService {

    protected final IOrderRepository repository;

    protected final IProductPort port;

    public AbstractOrderService(IOrderRepository repository, IProductPort port) {
        this.repository = repository;
        this.port = port;
    }

    @Override
    public PayOrderEntity createOrder(ShopCartEntity shopCartEntity) throws Exception {
        // 1. 鍏堟煡鐢ㄦ埛鏄惁宸叉湁鏈敮浠樿鍗曘€傝繖鏍烽噸澶嶇偣鍑烩€滅珛鍗虫敮浠樷€濅笉浼氶噸澶嶅垱寤鸿鍗曘€?        OrderEntity unpaidOrderEntity = repository.queryUnPayOrder(shopCartEntity);

        if (null != unpaidOrderEntity && OrderStatusVO.PAY_WAIT.equals(unpaidOrderEntity.getOrderStatusVO())) {
            log.info("鍒涘缓璁㈠崟-瀛樺湪锛屽凡瀛樺湪鏈敮浠樿鍗曘€倁serId:{} productId:{} orderId:{}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrderEntity.getOrderId());
            return PayOrderEntity.builder()
                    .orderId(unpaidOrderEntity.getOrderId())
                    .payUrl(unpaidOrderEntity.getPayUrl())
                    .build();
        } else if (null != unpaidOrderEntity && OrderStatusVO.CREATE.equals(unpaidOrderEntity.getOrderStatusVO())) {
            log.info("鍒涘缓璁㈠崟-瀛樺湪锛屽瓨鍦ㄦ湭鍒涘缓鏀粯鍗曡鍗曪紝鍒涘缓鏀粯鍗曞紑濮?userId:{} productId:{} orderId:{}", shopCartEntity.getUserId(), shopCartEntity.getProductId(), unpaidOrderEntity.getOrderId());
            Integer marketType = unpaidOrderEntity.getMarketType();
            BigDecimal marketDeductionAmount = unpaidOrderEntity.getMarketDeductionAmount();

            PayOrderEntity payOrderEntity = null;

            if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(marketType) && null == marketDeductionAmount) {
                // 鏈湴璁㈠崟宸插垱寤轰絾杩樻病閿佸畾鎷煎洟浼樻儬鏃讹紝鍏堣ˉ鍋氳惀閿€閿佸崟锛屽啀鍒涘缓鏀粯瀹濇敮浠樺崟銆?                MarketPayDiscountEntity marketPayDiscountEntity = this.lockMarketPayOrder(shopCartEntity.getUserId(),
                        shopCartEntity.getTeamId(),
                        shopCartEntity.getActivityId(),
                        shopCartEntity.getProductId(),
                        unpaidOrderEntity.getOrderId());

                payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount(), marketPayDiscountEntity);
            } else if (MarketTypeVO.GROUP_BUY_MARKET.getCode().equals(marketType)) {
                payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getPayAmount());
            } else {
                payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(), shopCartEntity.getProductId(),
                        unpaidOrderEntity.getProductName(), unpaidOrderEntity.getOrderId(), unpaidOrderEntity.getTotalAmount());
            }

            return PayOrderEntity.builder()
                    .orderId(payOrderEntity.getOrderId())
                    .payUrl(payOrderEntity.getPayUrl())
                    .build();
        }

        // 2. 娌℃湁鏈敮浠樿鍗曟椂锛屾煡璇㈠晢鍝佷俊鎭苟鍒涘缓鍏ㄦ柊鐨勬湰鍦拌鍗曘€?        ProductEntity productEntity = port.queryProductByProductId(shopCartEntity.getProductId());

        // 3. 璁㈠崟瀹炰綋淇濆瓨璁㈠崟鍙枫€佽鍗曟椂闂淬€佽鍗曠姸鎬併€佽惀閿€绫诲瀷绛変笟鍔″睘鎬с€?        OrderEntity orderEntity = CreateOrderAggregate.buildOrderEntity(productEntity.getProductId(), productEntity.getProductName(), shopCartEntity.getMarketTypeVO().getCode());

        // 4. 鑱氬悎瀵硅薄鎶婄敤鎴枫€佸晢鍝併€佽鍗曠粍鍚堣捣鏉ワ紝鐢变粨鍌ㄥ眰涓€娆℃€т繚瀛樸€?        CreateOrderAggregate orderAggregate = CreateOrderAggregate.builder()
                .userId(shopCartEntity.getUserId())
                .productEntity(productEntity)
                .orderEntity(orderEntity)
                .build();

        // 5. 鍏堜繚瀛樺晢鍩庢湰鍦拌鍗曪紝鍐嶈姹傚閮ㄦ敮浠橈紱杩欐牱鏀粯澶辫触涔熸湁璁㈠崟鐘舵€佸彲杩借釜銆?        this.doSaveOrder(orderAggregate);

        // 6. 濡傛灉閫夋嫨鎷煎洟钀ラ攢锛岃皟鐢ㄦ嫾鍥㈡湇鍔￠攣鍗曪紝杩斿洖浼樻儬閲戦鍜屽疄闄呮敮浠橀噾棰濄€?        MarketPayDiscountEntity marketPayDiscountEntity = null;
        if (MarketTypeVO.GROUP_BUY_MARKET.equals(shopCartEntity.getMarketTypeVO())) {
            marketPayDiscountEntity = this.lockMarketPayOrder(shopCartEntity.getUserId(),
                    shopCartEntity.getTeamId(),
                    shopCartEntity.getActivityId(),
                    shopCartEntity.getProductId(),
                    orderEntity.getOrderId());
        }

        // 7. 璋冩敮浠樺疂棰勬敮浠樻帴鍙ｏ紝鐢熸垚鍓嶇鍙墦寮€鐨勬敮浠樿〃鍗?閾炬帴銆?        PayOrderEntity payOrderEntity = doPrepayOrder(shopCartEntity.getUserId(),
                productEntity.getProductId(),
                productEntity.getProductName(),
                orderEntity.getOrderId(),
                productEntity.getPrice(),
                marketPayDiscountEntity);

        log.info("鍒涘缓璁㈠崟-瀹屾垚锛岀敓鎴愭敮浠樺崟銆倁serId: {} orderId: {} payUrl: {}", shopCartEntity.getUserId(), orderEntity.getOrderId(), payOrderEntity.getPayUrl());

        return PayOrderEntity.builder()
                .orderId(orderEntity.getOrderId())
                .payUrl(payOrderEntity.getPayUrl())
                .build();
    }

    protected abstract void doSaveOrder(CreateOrderAggregate orderAggregate);

    protected abstract MarketPayDiscountEntity lockMarketPayOrder(String userId, String teamId, Long activityId, String productId, String orderId);

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount) throws AlipayApiException;

    protected abstract PayOrderEntity doPrepayOrder(String userId, String productId, String productName, String orderId, BigDecimal totalAmount, MarketPayDiscountEntity marketPayDiscountEntity) throws AlipayApiException;

    @Override
    public List<OrderEntity> queryUserOrderList(String userId, Long lastId, Integer pageSize) {
        return repository.queryUserOrderList(userId, lastId, pageSize);
    }

}
