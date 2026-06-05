package cn.bugstack.domain.order.model.entity;

import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayOrderEntity {

    private String userId;
    private String orderId;
    private String payUrl;
    private OrderStatusVO orderStatus;

    // 钀ラ攢绫诲瀷锛?鏃犺惀閿€銆?鎷煎洟钀ラ攢
    private Integer marketType;
    // 钀ラ攢閲戦锛涗紭鎯犻噾棰?
    private BigDecimal marketDeductionAmount;
    // 鏀粯閲戦
    private BigDecimal payAmount;

}
