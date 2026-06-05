package cn.bugstack.infrastructure.gateway.dto;

import lombok.Data;

/**
 * @description 鑾峰彇寰俊鐧诲綍浜岀淮鐮佸搷搴斿璞?
 */
@Data
public class WeixinQrCodeResponseDTO {

    private String ticket;
    private Long expire_seconds;
    private String url;

}
