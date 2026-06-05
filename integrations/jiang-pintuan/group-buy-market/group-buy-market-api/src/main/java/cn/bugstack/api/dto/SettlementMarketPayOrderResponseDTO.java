package cn.bugstack.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 缁撶畻搴旂瓟瀵硅薄
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementMarketPayOrderResponseDTO {

    /** 鐢ㄦ埛ID */
    private String userId;
    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 娲诲姩ID */
    private Long activityId;
    /** 澶栭儴浜ゆ槗鍗曞彿 */
    private String outTradeNo;

}
