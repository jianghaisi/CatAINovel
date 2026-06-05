package cn.bugstack.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 鎷煎洟浜ゆ槗缁撶畻瑙勫垯鍛戒护
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeSettlementRuleCommandEntity {

    /** 娓犻亾 */
    private String source;
    /** 鏉ユ簮 */
    private String channel;
    /** 鐢ㄦ埛ID */
    private String userId;
    /** 澶栭儴浜ゆ槗鍗曞彿 */
    private String outTradeNo;
    /** 澶栭儴浜ゆ槗鏃堕棿 */
    private Date outTradeTime;

}
