package cn.bugstack.infrastructure.gateway.dto;

import lombok.Data;

/**
 * @description 鑾峰彇 Access token DTO 瀵硅薄
 */
@Data
public class WeixinTokenResponseDTO {

    private String access_token;
    private int expires_in;
    private String errcode;
    private String errmsg;

}
