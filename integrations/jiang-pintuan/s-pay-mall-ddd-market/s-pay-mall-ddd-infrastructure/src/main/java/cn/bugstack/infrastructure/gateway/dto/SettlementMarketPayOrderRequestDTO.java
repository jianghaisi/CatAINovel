package cn.bugstack.infrastructure.gateway.dto;

import lombok.Data;

import java.util.Date;

/**
 * @description 缁撶畻璇锋眰瀵硅薄
 */
@Data
public class SettlementMarketPayOrderRequestDTO {

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
