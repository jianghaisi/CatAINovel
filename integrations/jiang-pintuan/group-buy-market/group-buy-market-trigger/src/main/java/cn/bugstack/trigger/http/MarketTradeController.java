package cn.bugstack.trigger.http;

import cn.bugstack.api.IMarketTradeService;
import cn.bugstack.api.dto.*;
import cn.bugstack.api.response.Response;
import cn.bugstack.domain.activity.model.entity.MarketProductEntity;
import cn.bugstack.domain.activity.model.entity.TrialBalanceEntity;
import cn.bugstack.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.model.valobj.TeamStatisticVO;
import cn.bugstack.domain.activity.service.IIndexGroupBuyMarketService;
import cn.bugstack.domain.trade.model.entity.*;
import cn.bugstack.domain.trade.model.valobj.GroupBuyProgressVO;
import cn.bugstack.domain.trade.model.valobj.NotifyConfigVO;
import cn.bugstack.domain.trade.model.valobj.NotifyTypeEnumVO;
import cn.bugstack.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import cn.bugstack.domain.trade.service.ITradeLockOrderService;
import cn.bugstack.domain.trade.service.ITradeRefundOrderService;
import cn.bugstack.domain.trade.service.ITradeSettlementOrderService;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @description 钀ラ攢浜ゆ槗鏈嶅姟
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/trade/")
public class MarketTradeController implements IMarketTradeService {

    // 璇曠畻鏈嶅姟锛氶攣鍗曞墠鍐嶆璁＄畻浠锋牸鍜屾牎楠岀敤鎴锋槸鍚﹀彲瑙併€佸彲鍙備笌銆?    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;
    // 閿佸崟鏈嶅姟锛氬垱寤烘嫾鍥㈤槦浼嶆垨鍔犲叆宸叉湁闃熶紞锛屽苟鍐欏叆鎷煎洟瀛愬崟銆?    @Resource
    private ITradeLockOrderService tradeOrderService;
    // 缁撶畻鏈嶅姟锛氭敮浠樻垚鍔熷悗鏇存柊鎷煎洟瀹屾垚鏁帮紝鍒ゆ柇鏄惁鎴愬洟銆?    @Resource
    private ITradeSettlementOrderService tradeSettlementOrderService;
    // 閫€鍗曟湇鍔★細鐢ㄦ埛鍙栨秷銆佽秴鏃舵湭鏀粯銆佹垚鍥㈠悗閫€娆剧瓑閫嗗悜娴佺▼鍏ュ彛銆?    @Resource
    private ITradeRefundOrderService tradeRefundOrderService;

    /**
     * 鎷煎洟钀ラ攢閿佸崟
     */
    @RequestMapping(value = "lock_market_pay_order", method = RequestMethod.POST)
    @Override
    public Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(@RequestBody LockMarketPayOrderRequestDTO requestDTO) {
        try {
            // Controller 灞傚彧鍋氬弬鏁版媶瑙ｃ€佸熀纭€鏍￠獙鍜?DTO/棰嗗煙瀵硅薄杞崲锛屼笉鐩存帴鍐欐暟鎹簱銆?            String userId = requestDTO.getUserId();
            String source = requestDTO.getSource();
            String channel = requestDTO.getChannel();
            String goodsId = requestDTO.getGoodsId();
            Long activityId = requestDTO.getActivityId();
            String outTradeNo = requestDTO.getOutTradeNo();
            String teamId = requestDTO.getTeamId();
            LockMarketPayOrderRequestDTO.NotifyConfigVO notifyConfigVO = requestDTO.getNotifyConfigVO();

            log.info("钀ラ攢浜ゆ槗閿佸崟:{} LockMarketPayOrderRequestDTO:{}", userId, JSON.toJSONString(requestDTO));

            if (StringUtils.isBlank(userId) || StringUtils.isBlank(source) || StringUtils.isBlank(channel) || StringUtils.isBlank(goodsId) || null == activityId || ("HTTP".equals(notifyConfigVO.getNotifyType()) && StringUtils.isBlank(notifyConfigVO.getNotifyUrl()))) {
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 骞傜瓑鏍￠獙锛氬悓涓€涓閮ㄤ氦鏄撳彿 outTradeNo 閲嶅璇锋眰鏃讹紝鐩存帴杩斿洖鍘熸潵鐨勯攣鍗曠粨鏋滐紝閬垮厤閲嶅鍗犱綅銆?            MarketPayOrderEntity marketPayOrderEntity = tradeOrderService.queryNoPayMarketPayOrderByOutTradeNo(userId, outTradeNo);
            if (null != marketPayOrderEntity && TradeOrderStatusEnumVO.CREATE.equals(marketPayOrderEntity.getTradeOrderStatusEnumVO())) {
                LockMarketPayOrderResponseDTO lockMarketPayOrderResponseDTO = LockMarketPayOrderResponseDTO.builder()
                        .orderId(marketPayOrderEntity.getOrderId())
                        .originalPrice(marketPayOrderEntity.getOriginalPrice())
                        .deductionPrice(marketPayOrderEntity.getDeductionPrice())
                        .payPrice(marketPayOrderEntity.getPayPrice())
                        .tradeOrderStatus(marketPayOrderEntity.getTradeOrderStatusEnumVO().getCode())
                        .build();

                log.info("浜ゆ槗閿佸崟璁板綍(瀛樺湪):{} marketPayOrderEntity:{}", userId, JSON.toJSONString(marketPayOrderEntity));
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.SUCCESS.getCode())
                        .info(ResponseCode.SUCCESS.getInfo())
                        .data(lockMarketPayOrderResponseDTO)
                        .build();
            }

            // 鍙傚洟鏃跺厛蹇€熷垽鏂槦浼嶆槸鍚﹀凡婊★紱鏈€缁堥槻瓒呭崠浠嶈渚濊禆 domain/repository 涓殑瑙勫垯閾惧拰鏉′欢鏇存柊銆?            if (StringUtils.isNotBlank(teamId)) {
                GroupBuyProgressVO groupBuyProgressVO = tradeOrderService.queryGroupBuyProgress(teamId);
                if (null != groupBuyProgressVO && Objects.equals(groupBuyProgressVO.getTargetCount(), groupBuyProgressVO.getLockCount())) {
                    log.info("浜ゆ槗閿佸崟鎷︽埅-鎷煎崟鐩爣宸茶揪鎴?{} {}", userId, teamId);
                    return Response.<LockMarketPayOrderResponseDTO>builder()
                            .code(ResponseCode.E0006.getCode())
                            .info(ResponseCode.E0006.getInfo())
                            .build();
                }
            }

            // 閿佸崟鍓嶉噸鏂拌瘯绠楋紝閬垮厤鍓嶇灞曠ず浠疯繃鏈熸垨鐢ㄦ埛涓嶆弧瓒冲綋鍓嶆椿鍔ㄨ鍒欍€?            TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                    .userId(userId)
                    .source(source)
                    .channel(channel)
                    .goodsId(goodsId)
                    .activityId(activityId)
                    .build());

            // 浜虹兢闄愬埗锛歩sVisible 鎺у埗鏄惁鑳界湅鍒版椿鍔紝isEnable 鎺у埗鏄惁鑳藉弬涓庢椿鍔ㄣ€?            if (!trialBalanceEntity.getIsVisible() || !trialBalanceEntity.getIsEnable()) {
                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.E0007.getCode())
                        .info(ResponseCode.E0007.getInfo())
                        .build();
            }

            GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();

            // 璋冪敤棰嗗煙鏈嶅姟閿佸崟锛氬彲鑳藉垱寤烘柊鍥紝涔熷彲鑳藉姞鍏ヨ€佸洟锛涘悓鏃朵繚瀛樹紭鎯犱环鏍煎拰鍥炶皟閰嶇疆銆?            marketPayOrderEntity = tradeOrderService.lockMarketPayOrder(
                    UserEntity.builder().userId(userId).build(),
                    PayActivityEntity.builder()
                            .teamId(teamId)
                            .activityId(activityId)
                            .activityName(groupBuyActivityDiscountVO.getActivityName())
                            .startTime(groupBuyActivityDiscountVO.getStartTime())
                            .endTime(groupBuyActivityDiscountVO.getEndTime())
                            .validTime(groupBuyActivityDiscountVO.getValidTime())
                            .targetCount(groupBuyActivityDiscountVO.getTarget())
                            .build(),
                    PayDiscountEntity.builder()
                            .source(source)
                            .channel(channel)
                            .goodsId(goodsId)
                            .goodsName(trialBalanceEntity.getGoodsName())
                            .originalPrice(trialBalanceEntity.getOriginalPrice())
                            .deductionPrice(trialBalanceEntity.getDeductionPrice())
                            .payPrice(trialBalanceEntity.getPayPrice())
                            .outTradeNo(outTradeNo)
                            .notifyConfigVO(
                                    // 鏋勫缓鍥炶皟閫氱煡瀵硅薄
                                    NotifyConfigVO.builder()
                                            .notifyType(NotifyTypeEnumVO.valueOf(notifyConfigVO.getNotifyType()))
                                            .notifyMQ(notifyConfigVO.getNotifyMQ())
                                            .notifyUrl(notifyConfigVO.getNotifyUrl())
                                            .build())
                            .build());

            log.info("浜ゆ槗閿佸崟璁板綍(鏂?:{} marketPayOrderEntity:{}", userId, JSON.toJSONString(marketPayOrderEntity));

            // 杩斿洖缁撴灉
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(LockMarketPayOrderResponseDTO.builder()
                            .orderId(marketPayOrderEntity.getOrderId())
                            .originalPrice(marketPayOrderEntity.getOriginalPrice())
                            .deductionPrice(marketPayOrderEntity.getDeductionPrice())
                            .payPrice(marketPayOrderEntity.getPayPrice())
                            .tradeOrderStatus(marketPayOrderEntity.getTradeOrderStatusEnumVO().getCode())
                            .teamId(marketPayOrderEntity.getTeamId())
                            .build())
                    .build();
        } catch (AppException e) {
            log.error("钀ラ攢浜ゆ槗閿佸崟涓氬姟寮傚父:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("钀ラ攢浜ゆ槗閿佸崟鏈嶅姟澶辫触:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "settlement_market_pay_order", method = RequestMethod.POST)
    @Override
    public Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(@RequestBody SettlementMarketPayOrderRequestDTO requestDTO) {
        try {
            log.info("钀ラ攢浜ゆ槗缁勯槦缁撶畻寮€濮?{} outTradeNo:{}", requestDTO.getUserId(), requestDTO.getOutTradeNo());

            if (StringUtils.isBlank(requestDTO.getUserId()) || StringUtils.isBlank(requestDTO.getSource()) || StringUtils.isBlank(requestDTO.getChannel()) || StringUtils.isBlank(requestDTO.getOutTradeNo()) || null == requestDTO.getOutTradeTime()) {
                return Response.<SettlementMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 鏀粯绯荤粺纭鏀粯鎴愬姛鍚庤皟鐢細鏇存柊瀛愬崟涓哄凡鏀粯锛屽鍔犻槦浼嶅畬鎴愪汉鏁帮紝婊″憳鏃惰Е鍙戞垚鍥㈤€氱煡銆?            TradePaySettlementEntity tradePaySettlementEntity = tradeSettlementOrderService.settlementMarketPayOrder(TradePaySuccessEntity.builder()
                    .source(requestDTO.getSource())
                    .channel(requestDTO.getChannel())
                    .userId(requestDTO.getUserId())
                    .outTradeNo(requestDTO.getOutTradeNo())
                    .outTradeTime(requestDTO.getOutTradeTime())
                    .build());

            SettlementMarketPayOrderResponseDTO responseDTO = SettlementMarketPayOrderResponseDTO.builder()
                    .userId(tradePaySettlementEntity.getUserId())
                    .teamId(tradePaySettlementEntity.getTeamId())
                    .activityId(tradePaySettlementEntity.getActivityId())
                    .outTradeNo(tradePaySettlementEntity.getOutTradeNo())
                    .build();

            // 杩斿洖缁撴灉
            Response<SettlementMarketPayOrderResponseDTO> response = Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();

            log.info("钀ラ攢浜ゆ槗缁勯槦缁撶畻瀹屾垚:{} outTradeNo:{} response:{}", requestDTO.getUserId(), requestDTO.getOutTradeNo(), JSON.toJSONString(response));

            return response;
        } catch (AppException e) {
            log.error("钀ラ攢浜ゆ槗缁勯槦缁撶畻寮傚父:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("钀ラ攢浜ゆ槗缁勯槦缁撶畻澶辫触:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    @RequestMapping(value = "refund_market_pay_order", method = RequestMethod.POST)
    @Override
    public Response<RefundMarketPayOrderResponseDTO> refundMarketPayOrder(@RequestBody RefundMarketPayOrderRequestDTO requestDTO) {
        try {
            log.info("钀ラ攢鎷煎洟閫€鍗曞紑濮?{} outTradeNo:{}", requestDTO.getUserId(), requestDTO.getOutTradeNo());

            if (StringUtils.isBlank(requestDTO.getUserId()) || StringUtils.isBlank(requestDTO.getOutTradeNo()) || StringUtils.isBlank(requestDTO.getSource()) || StringUtils.isBlank(requestDTO.getChannel())) {
                return Response.<RefundMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            // 閫嗗悜娴佺▼鍏ュ彛锛氭牴鎹鍗曞綋鍓嶇姸鎬侀€夋嫨鈥滄湭鏀粯閫€鍗曘€佸凡鏀粯鏈垚鍥㈤€€鍗曘€佹垚鍥㈠悗閫€鍗曗€濈瓑绛栫暐銆?            TradeRefundBehaviorEntity tradeRefundBehaviorEntity = tradeRefundOrderService.refundOrder(TradeRefundCommandEntity.builder()
                    .userId(requestDTO.getUserId())
                    .outTradeNo(requestDTO.getOutTradeNo())
                    .source(requestDTO.getSource())
                    .channel(requestDTO.getChannel())
                    .build());

            RefundMarketPayOrderResponseDTO responseDTO = RefundMarketPayOrderResponseDTO.builder()
                    .userId(tradeRefundBehaviorEntity.getUserId())
                    .orderId(tradeRefundBehaviorEntity.getOrderId())
                    .teamId(tradeRefundBehaviorEntity.getTeamId())
                    .code(tradeRefundBehaviorEntity.getTradeRefundBehaviorEnum().getCode())
                    .info(tradeRefundBehaviorEntity.getTradeRefundBehaviorEnum().getInfo())
                    .build();

            // 杩斿洖缁撴灉
            Response<RefundMarketPayOrderResponseDTO> response = Response.<RefundMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(responseDTO)
                    .build();

            log.info("钀ラ攢鎷煎洟閫€鍗曞畬鎴?{} outTradeNo:{} response:{}", requestDTO.getUserId(), requestDTO.getOutTradeNo(), JSON.toJSONString(response));

            return response;
        } catch (AppException e) {
            log.error("钀ラ攢鎷煎洟閫€鍗曞紓甯?{} RefundMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<RefundMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            log.error("钀ラ攢鎷煎洟閫€鍗曞け璐?{} RefundMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);
            return Response.<RefundMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

}
