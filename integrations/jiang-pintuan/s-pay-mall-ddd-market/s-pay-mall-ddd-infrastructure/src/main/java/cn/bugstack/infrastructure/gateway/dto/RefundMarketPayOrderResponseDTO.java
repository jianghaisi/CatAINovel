package cn.bugstack.infrastructure.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 钀ラ攢鎷煎洟閫€鍗曞搷搴斿璞?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundMarketPayOrderResponseDTO {

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
     * 閫€鍗曡涓虹姸鎬佺爜
     */
    private String code;

    /**
     * 閫€鍗曡涓虹姸鎬佷俊鎭?
     */
    private String info;

}