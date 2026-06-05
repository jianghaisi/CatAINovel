package cn.bugstack.domain.trade.model.valobj;

import lombok.*;

/**
 * @description 鎷煎洟杩涘害鍊煎璞?
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyProgressVO {

    /** 鐩爣鏁伴噺 */
    private Integer targetCount;
    /** 瀹屾垚鏁伴噺 */
    private Integer completeCount;
    /** 閿佸崟鏁伴噺 */
    private Integer lockCount;

}
