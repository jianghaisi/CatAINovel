package cn.bugstack.domain.trade.model.entity;

import lombok.*;

/**
 * 閫€鍗曡鍔?
 *
 * 2025/7/12 07:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeRefundBehaviorEntity {

    /**
     * 鐢ㄦ埛ID
     */
    private String userId;

    /**
     * 璁㈠崟ID
     */
    private String orderId;

    /**
     * 缁勯槦ID
     */
    private String teamId;

    /**
     * 琛屼负鏋氫妇
     */
    private TradeRefundBehaviorEnum tradeRefundBehaviorEnum;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public enum TradeRefundBehaviorEnum {

        SUCCESS("success", "鎴愬姛"),
        REPEAT("repeat", "閲嶅"),
        FAIL("fail", "澶辫触"),

        ;

        private String code;
        private String info;
    }

}
