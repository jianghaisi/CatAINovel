package cn.bugstack.domain.trade.model.entity;

import cn.bugstack.types.enums.ActivityStatusEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎷煎洟娲诲姩瀹炰綋瀵硅薄
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyActivityEntity {

    /** 娲诲姩ID */
    private Long activityId;
    /** 娲诲姩鍚嶇О */
    private String activityName;
    /** 鎶樻墸ID */
    private String discountId;
    /** 鎷煎洟鏂瑰紡锛?鑷姩鎴愬洟銆?杈炬垚鐩爣鎷煎洟锛?*/
    private Integer groupType;
    /** 鎷煎洟娆℃暟闄愬埗 */
    private Integer takeLimitCount;
    /** 鎷煎洟鐩爣 */
    private Integer target;
    /** 鎷煎洟鏃堕暱锛堝垎閽燂級 */
    private Integer validTime;
    /** 娲诲姩鐘舵€侊紙0鍒涘缓銆?鐢熸晥銆?杩囨湡銆?搴熷純锛?*/
    private ActivityStatusEnumVO status;
    /** 娲诲姩寮€濮嬫椂闂?*/
    private Date startTime;
    /** 娲诲姩缁撴潫鏃堕棿 */
    private Date endTime;
    /** 浜虹兢鏍囩瑙勫垯鏍囪瘑 */
    private String tagId;
    /** 浜虹兢鏍囩瑙勫垯鑼冨洿 */
    private String tagScope;

}
