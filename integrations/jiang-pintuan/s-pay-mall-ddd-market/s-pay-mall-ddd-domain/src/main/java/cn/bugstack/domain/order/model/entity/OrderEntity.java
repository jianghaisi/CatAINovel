package cn.bugstack.domain.order.model.entity;

import cn.bugstack.domain.order.model.valobj.OrderStatusVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    // 涓婚敭ID
    private Long id;
    // 鐢ㄦ埛ID
    private String userId;
    private String productId;
    private String productName;
    private String orderId;
    private Date orderTime;
    private BigDecimal totalAmount;
    private OrderStatusVO orderStatusVO;
    private String payUrl;
    // 钀ラ攢绫诲瀷锛?鏃犺惀閿€銆?鎷煎洟钀ラ攢
    private Integer marketType;
    // 钀ラ攢閲戦锛涗紭鎯犻噾棰?
    private BigDecimal marketDeductionAmount;
    // 鏀粯閲戦
    private BigDecimal payAmount;
    // 鏀粯鏃堕棿
    private Date payTime;

}
