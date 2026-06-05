package cn.bugstack.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 浠诲姟绫诲瀷鏋氫妇
 *
 * 2025/7/18 21:35
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TaskNotifyCategoryEnumVO {

    TRADE_SETTLEMENT("trade_settlement","浜ゆ槗缁撶畻"),
    TRADE_UNPAID2REFUND("trade_unpaid2refund","浜ゆ槗閫€鍗?鏈敮浠?鏈垚鍥?),
    TRADE_PAID2REFUND("trade_paid2refund","浜ゆ槗閫€鍗?宸叉敮浠?鏈垚鍥?),
    TRADE_PAID_TEAM2REFUND("trade_paid_team2refund","浜ゆ槗閫€鍗?宸叉敮浠?宸叉垚鍥?),

    ;

    private String code;
    private String info;

    /**
     * 鏍规嵁code鍊艰幏鍙栧搴旂殑鏋氫妇
     */
    public static TaskNotifyCategoryEnumVO getByCode(String code) {
        for (TaskNotifyCategoryEnumVO enumVO : values()) {
            if (enumVO.getCode().equals(code)) {
                return enumVO;
            }
        }
        throw new RuntimeException("浠诲姟閫氱煡绫诲瀷鏋氫妇鍊间笉瀛樺湪: " + code);
    }

}
