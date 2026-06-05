package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎷煎洟缁勯槦瀹炰綋瀵硅薄
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupBuyOrderDetailEntity {

    /** 鐢ㄦ埛ID */
    private String userId;
    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 娲诲姩ID */
    private Long activityId;
    /** 鐩爣鏁伴噺 */
    private Integer targetCount;
    /** 瀹屾垚鏁伴噺 */
    private Integer completeCount;
    /** 閿佸崟鏁伴噺 */
    private Integer lockCount;
    /** 鎷煎洟寮€濮嬫椂闂?- 鍙備笌鎷煎洟鏃堕棿 */
    private Date validStartTime;
    /** 鎷煎洟缁撴潫鏃堕棿 - 鎷煎洟鏈夋晥鏃堕暱 */
    private Date validEndTime;
    /** 澶栭儴浜ゆ槗鍗曞彿-纭繚澶栭儴璋冪敤鍞竴骞傜瓑 */
    private String outTradeNo;
    /** 娓犻亾 */
    private String source;
    /** 鏉ユ簮 */
    private String channel;

}
