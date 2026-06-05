package cn.bugstack.domain.trade.model.entity;

import cn.bugstack.domain.trade.model.valobj.NotifyConfigVO;
import cn.bugstack.domain.trade.model.valobj.NotifyTypeEnumVO;
import cn.bugstack.types.enums.GroupBuyOrderEnumVO;
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
public class GroupBuyTeamEntity {

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
    /** 鐘舵€侊紙0-鎷煎崟涓€?-瀹屾垚銆?-澶辫触锛?*/
    private GroupBuyOrderEnumVO status;
    /** 鎷煎洟寮€濮嬫椂闂?- 鍙備笌鎷煎洟鏃堕棿 */
    private Date validStartTime;
    /** 鎷煎洟缁撴潫鏃堕棿 - 鎷煎洟鏈夋晥鏃堕暱 */
    private Date validEndTime;
    /** 鍥炶皟閰嶇疆 */
    private NotifyConfigVO notifyConfigVO;

}
