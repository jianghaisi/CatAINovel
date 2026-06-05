package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 鐢ㄦ埛鎷煎崟
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyOrder {

    /** 鑷ID */
    private Long id;
    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 娲诲姩ID */
    private Long activityId;
    /** 娓犻亾 */
    private String source;
    /** 鏉ユ簮 */
    private String channel;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    /** 鎶樻墸閲戦 */
    private BigDecimal deductionPrice;
    /** 鏀粯浠锋牸 */
    private BigDecimal payPrice;
    /** 鐩爣鏁伴噺 */
    private Integer targetCount;
    /** 瀹屾垚鏁伴噺 */
    private Integer completeCount;
    /** 閿佸崟鏁伴噺 */
    private Integer lockCount;
    /** 鐘舵€侊紙0-鎷煎崟涓€?-瀹屾垚銆?-澶辫触锛?*/
    private Integer status;
    /** 鎷煎洟寮€濮嬫椂闂?- 鍙備笌鎷煎洟鏃堕棿 */
    private Date validStartTime;
    /** 鎷煎洟缁撴潫鏃堕棿 - 鎷煎洟鏈夋晥鏃堕暱 */
    private Date validEndTime;
    /** 鍥炶皟绫诲瀷 HTTP銆丮Q */
    private String notifyType;
    /** 鍥炶皟閫氱煡锛圚TTP 鏂瑰紡鍥炶皟锛屽湴鍧€涓嶅彲涓虹┖锛?*/
    private String notifyUrl;
    /** 鍒涘缓鏃堕棿 */
    private Date createTime;
    /** 鏇存柊鏃堕棿 */
    private Date updateTime;

}
