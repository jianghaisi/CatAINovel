package cn.bugstack.domain.activity.model.entity;

import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @description 璇曠畻缁撴灉瀹炰綋瀵硅薄锛堢粰鐢ㄦ埛灞曠ず鎷煎洟鍙幏寰楃殑浼樻儬淇℃伅锛?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrialBalanceEntity {

    /** 鍟嗗搧ID */
    private String goodsId;
    /** 鍟嗗搧鍚嶇О */
    private String goodsName;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    // 鎶樻墸閲戦
    private BigDecimal deductionPrice;
    // 鏀粯閲戦
    private BigDecimal payPrice;
    /** 鎷煎洟鐩爣鏁伴噺 */
    private Integer targetCount;
    /** 鎷煎洟寮€濮嬫椂闂?*/
    private Date startTime;
    /** 鎷煎洟缁撴潫鏃堕棿 */
    private Date endTime;
    /** 鏄惁鍙鎷煎洟 */
    private Boolean isVisible;
    /** 鏄惁鍙弬涓庤繘鍥?*/
    private Boolean isEnable;

    /** 娲诲姩閰嶇疆淇℃伅 */
    private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;

}
