package cn.bugstack.domain.trade.model.valobj;

import lombok.*;

/**
 * 鎷煎洟閫€鍗曟秷鎭?
 *
 * 2025/7/29 09:15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRefundSuccess {

    /**
     * 閫€鍗曠被鍨?
     */
    private String type;

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
