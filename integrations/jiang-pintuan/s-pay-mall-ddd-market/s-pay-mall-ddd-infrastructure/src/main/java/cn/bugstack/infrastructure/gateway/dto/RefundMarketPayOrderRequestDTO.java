package cn.bugstack.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 钀ラ攢鎷煎洟閫€鍗曡姹傚璞?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundMarketPayOrderRequestDTO {

    /**
     * 鐢ㄦ埛ID
     */
    private String userId;

    /**
     * 澶栭儴浜ゆ槗鍗曞彿
     */
    private String outTradeNo;

    /**
     * 娓犻亾
     */
    private String source;

    /**
     * 鏉ユ簮
     */
    private String channel;

}