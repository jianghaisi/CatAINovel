package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鎷煎洟閫€鍗曟秷鎭璞?
 *
 * 2025/8/1 09:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamRefundSuccessRequestDTO {

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
