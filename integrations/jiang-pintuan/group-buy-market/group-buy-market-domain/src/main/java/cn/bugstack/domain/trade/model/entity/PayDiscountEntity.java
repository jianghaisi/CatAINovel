package cn.bugstack.domain.trade.model.entity;

import cn.bugstack.domain.trade.model.valobj.NotifyConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 鎷煎洟锛屾敮浠樹紭鎯犲疄浣撳璞?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayDiscountEntity {

    /** 娓犻亾 */
    private String source;
    /** 鏉ユ簮 */
    private String channel;
    /** 鍟嗗搧ID */
    private String goodsId;
    /** 鍟嗗搧鍚嶇О */
    private String goodsName;
    /** 鍘熷浠锋牸 */
    private BigDecimal originalPrice;
    /** 鎶樻墸閲戦 */
    private BigDecimal deductionPrice;
    /** 鏀粯閲戦 */
    private BigDecimal payPrice;
    /** 澶栭儴浜ゆ槗鍗曞彿-纭繚澶栭儴璋冪敤鍞竴骞傜瓑 */
    private String outTradeNo;
    /** 鍥炶皟閰嶇疆 */
    private NotifyConfigVO notifyConfigVO;

}
