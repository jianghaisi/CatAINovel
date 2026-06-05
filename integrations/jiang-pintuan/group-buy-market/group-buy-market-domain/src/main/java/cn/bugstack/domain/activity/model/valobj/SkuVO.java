package cn.bugstack.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 鍟嗗搧淇℃伅
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SkuVO {

    /** 鍟嗗搧ID */
    private String goodsId;
    /** 鍟嗗搧鍚嶇О */
    private String goodsName;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;

}
