package cn.bugstack.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 浜ゆ槗缁撶畻璁㈠崟瀹炰綋
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradePaySettlementEntity {

    /** 娓犻亾 */
    private String source;
    /** 鏉ユ簮 */
    private String channel;
    /** 鐢ㄦ埛ID */
    private String userId;
    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 娲诲姩ID */
    private Long activityId;
    /** 澶栭儴浜ゆ槗鍗曞彿 */
    private String outTradeNo;

}
