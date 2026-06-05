package cn.bugstack.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 鎷煎洟浜ゆ槗鍛戒护瀹炰綋
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeLockRuleCommandEntity {

    /** 鐢ㄦ埛ID */
    private String userId;
    /** 娲诲姩ID */
    private Long activityId;
    /** 缁勯槦ID */
    private String teamId;

}
