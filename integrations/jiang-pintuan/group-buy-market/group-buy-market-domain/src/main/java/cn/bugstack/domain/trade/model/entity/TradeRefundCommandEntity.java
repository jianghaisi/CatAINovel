package cn.bugstack.domain.trade.model.entity;

import cn.bugstack.domain.trade.model.valobj.RefundTypeEnumVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 閫€鍗曞疄浣撳璞?
 *
 * 2025/7/8 08:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeRefundCommandEntity {

    /**
     * 鐢ㄦ埛ID
     */
    private String userId;

    /**
     * 澶栭儴浜ゆ槗鍗曞彿
     */
    private String outTradeNo;

    /** 娓犻亾 */
    private String source;

    /** 鏉ユ簮 */
    private String channel;

}
