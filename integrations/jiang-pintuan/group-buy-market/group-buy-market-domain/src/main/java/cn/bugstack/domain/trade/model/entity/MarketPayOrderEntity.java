package cn.bugstack.domain.trade.model.entity;

import cn.bugstack.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 鎷煎洟锛岄璐鍗曡惀閿€瀹炰綋瀵硅薄
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketPayOrderEntity {

    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 棰勮喘璁㈠崟ID */
    private String orderId;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    /** 鎶樻墸閲戦 */
    private BigDecimal deductionPrice;
    /** 鏀粯閲戦 */
    private BigDecimal payPrice;
    /** 浜ゆ槗璁㈠崟鐘舵€佹灇涓?*/
    private TradeOrderStatusEnumVO tradeOrderStatusEnumVO;

}
