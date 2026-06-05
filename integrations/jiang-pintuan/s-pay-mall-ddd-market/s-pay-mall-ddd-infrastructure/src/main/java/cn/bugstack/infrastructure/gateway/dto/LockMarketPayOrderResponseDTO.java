package cn.bugstack.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 钀ラ攢鏀粯閿佸崟搴旂瓟瀵硅薄
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LockMarketPayOrderResponseDTO {

    /** 棰勮喘璁㈠崟ID */
    private String orderId;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    /** 鎶樻墸閲戦 */
    private BigDecimal deductionPrice;
    /** 鏀粯閲戦 */
    private BigDecimal payPrice;
    /** 浜ゆ槗璁㈠崟鐘舵€?*/
    private Integer tradeOrderStatus;
    /** 鎷煎洟缁勯槦ID */
    private String teamId;

}
