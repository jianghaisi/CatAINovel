package cn.bugstack.domain.order.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 钀ラ攢鏀粯浼樻儬
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPayDiscountEntity {

    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    /** 鎶樻墸閲戦 */
    private BigDecimal deductionPrice;
    /** 鏀粯閲戦 */
    private BigDecimal payPrice;

}
