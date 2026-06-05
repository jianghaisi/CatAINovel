package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 娓犻亾鍟嗗搧娲诲姩閰嶇疆鍊煎璞?
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SCSkuActivityVO {

    /** 娓犻亾 */
    private String source;
    /** 鏉ユ簮 */
    private String chanel;
    /** 娲诲姩ID */
    private Long activityId;
    /** 鍟嗗搧ID */
    private String goodsId;

}
