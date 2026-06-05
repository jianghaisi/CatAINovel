package cn.bugstack.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @description 鍥炶皟璇锋眰瀵硅薄
 */
@Data
public class NotifyRequestDTO {

    /** 缁勯槦ID */
    private String teamId;
    /** 澶栭儴鍗曞彿 */
    private List<String> outTradeNoList;

}
