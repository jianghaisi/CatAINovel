package cn.bugstack.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎷煎洟锛屾敮浠樻椿鍔ㄥ疄浣撳璞?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayActivityEntity {

    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 娲诲姩ID */
    private Long activityId;
    /** 娲诲姩鍚嶇О */
    private String activityName;
    /** 鎷煎洟寮€濮嬫椂闂?*/
    private Date startTime;
    /** 鎷煎洟缁撴潫鏃堕棿 */
    private Date endTime;
    /** 鎷煎洟鏃堕暱锛堝垎閽燂級*/
    private Integer validTime;
    /** 鐩爣鏁伴噺 */
    private Integer targetCount;

}
