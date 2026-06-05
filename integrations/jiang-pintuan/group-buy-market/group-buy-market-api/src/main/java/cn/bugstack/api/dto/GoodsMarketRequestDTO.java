package cn.bugstack.api.dto;

import lombok.Data;

/**
 * @description 鍟嗗搧钀ラ攢璇锋眰瀵硅薄
 */
@Data
public class GoodsMarketRequestDTO {

    // 鐢ㄦ埛ID
    private String userId;
    // 娓犻亾
    private String source;
    // 鏉ユ簮
    private String channel;
    // 鍟嗗搧ID
    private String goodsId;

}
