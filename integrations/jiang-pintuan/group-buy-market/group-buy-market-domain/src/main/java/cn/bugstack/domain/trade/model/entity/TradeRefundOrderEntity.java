package cn.bugstack.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 浜ゆ槗閫€鍗曞疄浣撳璞?
 *
 * 2025/7/11 19:45
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeRefundOrderEntity {

    /**
     * 鐢ㄦ埛ID
     */
    private String userId;

    /**
     * 鎷煎崟缁勯槦ID
     */
    private String teamId;

    /**
     * 娲诲姩ID
     */
    private Long activityId;

    /**
     * 棰勮喘璁㈠崟ID
     */
    private String orderId;

    /**
     * 澶栭儴浜ゆ槗鍗曞彿
     */
    private String outTradeNo;

}
