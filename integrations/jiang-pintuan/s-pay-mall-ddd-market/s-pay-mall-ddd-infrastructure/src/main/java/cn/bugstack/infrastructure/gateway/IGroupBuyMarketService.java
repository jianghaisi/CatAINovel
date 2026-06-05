package cn.bugstack.infrastructure.gateway;

import cn.bugstack.infrastructure.gateway.dto.*;
import cn.bugstack.infrastructure.gateway.response.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * @description 鎷煎洟钀ラ攢
 */
public interface IGroupBuyMarketService {

    /**
     * 钀ラ攢閿佸崟
     *
     * @param requestDTO 閿佸崟鍟嗗搧淇℃伅
     * @return 閿佸崟缁撴灉淇℃伅
     */
    @POST("api/v1/gbm/trade/lock_market_pay_order")
    Call<Response<LockMarketPayOrderResponseDTO>> lockMarketPayOrder(@Body LockMarketPayOrderRequestDTO requestDTO);

    /**
     * 钀ラ攢缁撶畻
     *
     * @param requestDTO 缁撶畻鍟嗗搧淇℃伅
     * @return 缁撶畻缁撴灉淇℃伅
     */
    @POST("api/v1/gbm/trade/settlement_market_pay_order")
    Call<Response<SettlementMarketPayOrderResponseDTO>> settlementMarketPayOrder(@Body SettlementMarketPayOrderRequestDTO requestDTO);

    /**
     * 钀ラ攢鎷煎洟閫€鍗?
     *
     * @param requestDTO 閫€鍗曡姹備俊鎭?
     * @return 閫€鍗曠粨鏋滀俊鎭?
     */
    @POST("api/v1/gbm/trade/refund_market_pay_order")
    Call<Response<RefundMarketPayOrderResponseDTO>> refundMarketPayOrder(@Body RefundMarketPayOrderRequestDTO requestDTO);

}
