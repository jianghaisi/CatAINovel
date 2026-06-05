package cn.bugstack.trigger.http;

import cn.bugstack.api.IPayService;
import cn.bugstack.api.dto.CreatePayRequestDTO;
import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.api.dto.QueryOrderListRequestDTO;
import cn.bugstack.api.dto.QueryOrderListResponseDTO;
import cn.bugstack.api.dto.RefundOrderRequestDTO;
import cn.bugstack.api.dto.RefundOrderResponseDTO;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.order.model.entity.OrderEntity;
import cn.bugstack.domain.order.model.entity.PayOrderEntity;
import cn.bugstack.domain.order.model.entity.ShopCartEntity;
import cn.bugstack.domain.order.model.valobj.MarketTypeVO;
import cn.bugstack.domain.order.service.IOrderService;
import cn.bugstack.types.common.Constants;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeQueryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/alipay/")
public class AliPayController implements IPayService {

    @Value("${alipay.alipay_public_key}")
    private String alipayPublicKey;

    @Resource
    private IOrderService orderService;
    
    @Resource
    private AlipayClient alipayClient;

    /**
     * http://localhost:8080/api/v1/alipay/create_pay_order
     * <p>
     * {
     * "userId": "10001",
     * "productId": "100001"
     * }
     */
    @RequestMapping(value = "create_pay_order", method = RequestMethod.POST)
    @Override
    public Response<String> createPayOrder(@RequestBody CreatePayRequestDTO createPayRequestDTO) {
        try {
            log.info("鍟嗗搧涓嬪崟锛屾牴鎹晢鍝両D鍒涘缓鏀粯鍗曞紑濮?userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId());
            String userId = createPayRequestDTO.getUserId();
            String productId = createPayRequestDTO.getProductId();
            String teamId = createPayRequestDTO.getTeamId();
            Integer marketType = createPayRequestDTO.getMarketType();

            // 涓嬪崟鍏ュ彛锛氭櫘閫氳鍗曠洿鎺ュ垱寤烘敮浠樺疂鏀粯鍗曪紱鎷煎洟璁㈠崟浼氬厛鍘?group-buy-market 閿佸畾钀ラ攢鍗曘€?            PayOrderEntity payOrderEntity = orderService.createOrder(ShopCartEntity.builder()
                    .userId(userId)
                    .productId(productId)
                    .teamId(teamId)
                    .marketTypeVO(MarketTypeVO.valueOf(marketType))
                    .activityId(createPayRequestDTO.getActivityId())
                    .build());

            log.info("鍟嗗搧涓嬪崟锛屾牴鎹晢鍝両D鍒涘缓鏀粯鍗曞畬鎴?userId:{} productId:{} orderId:{}", userId, productId, payOrderEntity.getOrderId());
            return Response.<String>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(payOrderEntity.getPayUrl())
                    .build();
        } catch (Exception e) {
            log.error("鍟嗗搧涓嬪崟锛屾牴鎹晢鍝両D鍒涘缓鏀粯鍗曞け璐?userId:{} productId:{}", createPayRequestDTO.getUserId(), createPayRequestDTO.getUserId(), e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "group_buy_notify", method = RequestMethod.POST)
    @Override
    public String groupBuyNotify(@RequestBody NotifyRequestDTO requestDTO) {
        log.info("鎷煎洟鍥炶皟锛岀粍闃熷畬鎴愶紝缁撶畻寮€濮?{}", JSON.toJSONString(requestDTO));
        try {
            // 鎷煎洟鏈嶅姟鎴愬洟鍚庡洖璋冨晢鍩庯細鍟嗗煄鎶婅繖鎵硅鍗曟洿鏂颁负鈥滆惀閿€缁撶畻瀹屾垚鈥濓紝鍚庣画鍙彂璐с€?            orderService.changeOrderMarketSettlement(requestDTO.getOutTradeNoList());
            return "success";
        } catch (Exception e) {
            log.error("鎷煎洟鍥炶皟锛岀粍闃熷畬鎴愶紝缁撶畻澶辫触 {}", JSON.toJSONString(requestDTO), e);
            return "error";
        }
    }

    /**
     * http://xfg-studio.natapp1.cc/api/v1/alipay/alipay_notify_url
     */
    @RequestMapping(value = "alipay_notify_url", method = RequestMethod.POST)
    public String payNotify(HttpServletRequest request) throws AlipayApiException, ParseException {
        log.info("鏀粯鍥炶皟锛屾秷鎭帴鏀?{}", request.getParameter("trade_status"));

        if (!request.getParameter("trade_status").equals("TRADE_SUCCESS")) {
            return "false";
        }

        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            params.put(name, request.getParameter(name));
        }

        String tradeNo = params.get("out_trade_no");
        String gmtPayment = params.get("gmt_payment");
        String alipayTradeNo = params.get("trade_no");

        String sign = params.get("sign");
        String content = AlipaySignature.getSignCheckContentV1(params);
        boolean checkSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayPublicKey, "UTF-8"); // 楠岃瘉绛惧悕
        // 鏀粯瀹濋獙绛?
        if (!checkSignature) {
            return "false";
        }

        // 鏀粯瀹濋獙绛鹃€氳繃鍚庯紝鎵嶅厑璁告妸鏈湴璁㈠崟鏀逛负鏀粯鎴愬姛锛岄槻姝吉閫犲洖璋冦€?        log.info("鏀粯鍥炶皟锛屼氦鏄撳悕绉? {}", params.get("subject"));
        log.info("鏀粯鍥炶皟锛屼氦鏄撶姸鎬? {}", params.get("trade_status"));
        log.info("鏀粯鍥炶皟锛屾敮浠樺疂浜ゆ槗鍑瘉鍙? {}", params.get("trade_no"));
        log.info("鏀粯鍥炶皟锛屽晢鎴疯鍗曞彿: {}", params.get("out_trade_no"));
        log.info("鏀粯鍥炶皟锛屼氦鏄撻噾棰? {}", params.get("total_amount"));
        log.info("鏀粯鍥炶皟锛屼拱瀹跺湪鏀粯瀹濆敮涓€id: {}", params.get("buyer_id"));
        log.info("鏀粯鍥炶皟锛屼拱瀹朵粯娆炬椂闂? {}", params.get("gmt_payment"));
        log.info("鏀粯鍥炶皟锛屼拱瀹朵粯娆鹃噾棰? {}", params.get("buyer_pay_amount"));
        log.info("鏀粯鍥炶皟锛屾敮浠樺洖璋冿紝鏇存柊璁㈠崟 {}", tradeNo);

        // 濡傛灉鏄嫾鍥㈣鍗曪紝OrderService 浼氱户缁皟鐢ㄦ嫾鍥㈡湇鍔″仛鏀粯缁撶畻銆?        orderService.changeOrderPaySuccess(tradeNo, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.get("gmt_payment")));

        return "success";
    }

    /**
     * http://localhost:8080/api/v1/alipay/query_user_order_list
     * <p>
     * {
     * "userId": "10001",
     * "lastId": null,
     * "pageSize": 10
     * }
     */
    @RequestMapping(value = "query_user_order_list", method = RequestMethod.POST)
    @Override
    public Response<QueryOrderListResponseDTO> queryUserOrderList(@RequestBody QueryOrderListRequestDTO requestDTO) {
        try {
            log.info("鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃寮€濮?userId:{} lastId:{} pageSize:{}", requestDTO.getUserId(), requestDTO.getLastId(), requestDTO.getPageSize());
            
            String userId = requestDTO.getUserId();
            Long lastId = requestDTO.getLastId();
            Integer pageSize = requestDTO.getPageSize();
            
            // 鏌ヨ璁㈠崟鍒楄〃锛屽鏌ヨ涓€鏉＄敤浜庡垽鏂槸鍚﹁繕鏈夋洿澶氭暟鎹?
            List<OrderEntity> orderList = orderService.queryUserOrderList(userId, lastId, pageSize + 1);
            
            // 鍒ゆ柇鏄惁杩樻湁鏇村鏁版嵁
            boolean hasMore = orderList.size() > pageSize;
            if (hasMore) {
                orderList = orderList.subList(0, pageSize);
            }
            
            // 杞崲涓哄搷搴斿璞?
            List<QueryOrderListResponseDTO.OrderInfo> orderInfoList = orderList.stream().map(order -> {
                QueryOrderListResponseDTO.OrderInfo orderInfo = new QueryOrderListResponseDTO.OrderInfo();
                orderInfo.setId(order.getId());
                orderInfo.setUserId(order.getUserId());
                orderInfo.setProductId(order.getProductId());
                orderInfo.setProductName(order.getProductName());
                orderInfo.setOrderId(order.getOrderId());
                orderInfo.setOrderTime(order.getOrderTime());
                orderInfo.setTotalAmount(order.getTotalAmount());
                orderInfo.setStatus(order.getOrderStatusVO() != null ? order.getOrderStatusVO().getCode() : null);
                orderInfo.setPayUrl(order.getPayUrl());
                orderInfo.setMarketType(order.getMarketType());
                orderInfo.setMarketDeductionAmount(order.getMarketDeductionAmount());
                orderInfo.setPayAmount(order.getPayAmount());
                orderInfo.setPayTime(order.getPayTime());
                return orderInfo;
            }).collect(Collectors.toList());
            
            QueryOrderListResponseDTO responseDTO = new QueryOrderListResponseDTO();
            responseDTO.setOrderList(orderInfoList);
            responseDTO.setHasMore(hasMore);
            responseDTO.setLastId(!orderList.isEmpty() ? orderList.get(orderList.size() - 1).getId() : null);
            
            log.info("鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃瀹屾垚 userId:{} 杩斿洖璁㈠崟鏁伴噺:{} hasMore:{}", userId, orderInfoList.size(), hasMore);
            return Response.<QueryOrderListResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("鏌ヨ鐢ㄦ埛璁㈠崟鍒楄〃澶辫触 userId:{}", requestDTO.getUserId(), e);
            return Response.<QueryOrderListResponseDTO>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * http://localhost:8080/api/v1/alipay/refund_order
     * <p>
     * {
     * "userId": "xfg02",
     * "orderId": "928263928388"
     * }
     */
    @RequestMapping(value = "refund_order", method = RequestMethod.POST)
    @Override
    public Response<RefundOrderResponseDTO> refundOrder(@RequestBody RefundOrderRequestDTO requestDTO) {
        try {
            log.info("鐢ㄦ埛閫€鍗曞紑濮?userId:{} orderId:{}", requestDTO.getUserId(), requestDTO.getOrderId());
            
            String userId = requestDTO.getUserId();
            String orderId = requestDTO.getOrderId();
            
            // 鐢ㄦ埛閫€鍗曞叆鍙ｏ細鍏堥€氱煡鎷煎洟鏈嶅姟閲婃斁/鍥炴粴钀ラ攢渚х姸鎬侊紝鍐嶆洿鏂板晢鍩庢湰鍦拌鍗曘€?            boolean success = orderService.refundMarketOrder(userId, orderId);
            
            RefundOrderResponseDTO responseDTO = new RefundOrderResponseDTO();
            responseDTO.setSuccess(success);
            responseDTO.setOrderId(orderId);
            responseDTO.setMessage(success ? "閫€鍗曟垚鍔? : "閫€鍗曞け璐ワ紝璁㈠崟涓嶅瓨鍦ㄣ€佸凡鍏抽棴鎴栦笉灞炰簬璇ョ敤鎴?);
            
            log.info("鐢ㄦ埛閫€鍗曞畬鎴?userId:{} orderId:{} success:{}", userId, orderId, success);
            return Response.<RefundOrderResponseDTO>builder()
                    .code(Constants.ResponseCode.SUCCESS.getCode())
                    .info(Constants.ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();
        } catch (Exception e) {
            log.error("鐢ㄦ埛閫€鍗曞け璐?userId:{} orderId:{}", requestDTO.getUserId(), requestDTO.getOrderId(), e);
            
            RefundOrderResponseDTO responseDTO = new RefundOrderResponseDTO();
            responseDTO.setSuccess(false);
            responseDTO.setOrderId(requestDTO.getOrderId());
            responseDTO.setMessage("閫€鍗曞け璐ワ紝绯荤粺寮傚父");
            
            return Response.<RefundOrderResponseDTO>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .data(responseDTO)
                    .build();
        }
    }

    /**
     * 娴嬭瘯鍥炶皟鎺ュ彛 - 涓诲姩鏌ヨ鏀粯瀹濅氦鏄撶姸鎬?
     * @param outTradeNo 鍟嗘埛璁㈠崟鍙?
     * @return 澶勭悊缁撴灉
     */
    @RequestMapping(value = "active_pay_notify", method = RequestMethod.POST)
    public Response<String> activePayNotify(@RequestParam String outTradeNo) {
        try {
            log.info("娴嬭瘯鍥炶皟鎺ュ彛锛屽紑濮嬫煡璇㈣鍗? {}", outTradeNo);
            
            // 鏋勫缓鏀粯瀹濅氦鏄撴煡璇㈣姹?
            AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
            bizModel.setOutTradeNo(outTradeNo);
            
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            queryRequest.setBizModel(bizModel);
            
            // 璋冪敤鏀粯瀹滱PI鏌ヨ浜ゆ槗鐘舵€?
            String body = alipayClient.execute(queryRequest).getBody();
            log.info("鏀粯瀹濇煡璇㈢粨鏋? {}", body);
            
            // 瑙ｆ瀽鏌ヨ缁撴灉
            JSONObject responseJson = JSON.parseObject(body);
            JSONObject queryResponse = responseJson.getJSONObject("alipay_trade_query_response");
            
            if (queryResponse != null && "10000".equals(queryResponse.getString("code"))) {
                String tradeStatus = queryResponse.getString("trade_status");
                String tradeNo = queryResponse.getString("trade_no");
                String totalAmount = queryResponse.getString("total_amount");
                String gmtPayment = queryResponse.getString("send_pay_date");
                
                log.info("鏌ヨ鎴愬姛 - 浜ゆ槗鐘舵€? {}, 鏀粯瀹濅氦鏄撳彿: {}, 閲戦: {}, 鏀粯鏃堕棿: {}", 
                        tradeStatus, tradeNo, totalAmount, gmtPayment);
                
                // 濡傛灉浜ゆ槗鎴愬姛锛屾墽琛屽悗缁祦绋嬪鐞?
                if ("TRADE_SUCCESS".equals(tradeStatus)) {
                    log.info("浜ゆ槗鎴愬姛锛屽紑濮嬪鐞嗗悗缁祦绋嬶紝璁㈠崟鍙? {}", outTradeNo);
                    
                    // 璋冪敤璁㈠崟鏈嶅姟鏇存柊璁㈠崟鐘舵€?
                    orderService.changeOrderPaySuccess(outTradeNo, 
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(gmtPayment));
                    
                    log.info("璁㈠崟鐘舵€佹洿鏂版垚鍔燂紝璁㈠崟鍙? {}", outTradeNo);
                    
                    return Response.<String>builder()
                            .code(Constants.ResponseCode.SUCCESS.getCode())
                            .info(Constants.ResponseCode.SUCCESS.getInfo())
                            .data("浜ゆ槗鎴愬姛锛岃鍗曠姸鎬佸凡鏇存柊")
                            .build();
                } else {
                    log.info("浜ゆ槗鐘舵€侀潪鎴愬姛鐘舵€? {}, 璁㈠崟鍙? {}", tradeStatus, outTradeNo);
                    return Response.<String>builder()
                            .code(Constants.ResponseCode.SUCCESS.getCode())
                            .info(Constants.ResponseCode.SUCCESS.getInfo())
                            .data("浜ゆ槗鐘舵€? " + tradeStatus)
                            .build();
                }
            } else {
                String errorMsg = queryResponse != null ? queryResponse.getString("msg") : "鏌ヨ澶辫触";
                log.error("鏀粯瀹濇煡璇㈠け璐? {}, 璁㈠崟鍙? {}", errorMsg, outTradeNo);
                return Response.<String>builder()
                        .code(Constants.ResponseCode.UN_ERROR.getCode())
                        .info(Constants.ResponseCode.UN_ERROR.getInfo())
                        .data("鏌ヨ澶辫触: " + errorMsg)
                        .build();
            }
            
        } catch (Exception e) {
            log.error("娴嬭瘯鍥炶皟鎺ュ彛寮傚父锛岃鍗曞彿: {}", outTradeNo, e);
            return Response.<String>builder()
                    .code(Constants.ResponseCode.UN_ERROR.getCode())
                    .info(Constants.ResponseCode.UN_ERROR.getInfo())
                    .data("绯荤粺寮傚父: " + e.getMessage())
                    .build();
        }
    }

}
