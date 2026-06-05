package cn.bugstack.api.dto;

import lombok.Data;

@Data
public class CreatePayRequestDTO {

    // 鐢ㄦ埛ID 銆愬疄闄呬骇鐢熶腑浼氶€氳繃鐧诲綍妯″潡鑾峰彇锛屼笉闇€瑕侀€忓交銆?
    private String userId;
    // 浜у搧缂栧彿
    private String productId;
    // 鎷煎洟闃熶紞 - 闃熶紞ID
    private String teamId;
    // 娲诲姩ID锛屾潵鑷簬椤甸潰璋冪敤鎷煎洟璇曠畻鍚庯紝鑾峰緱鐨勬椿鍔↖D淇℃伅
    private Long activityId;
    // 钀ラ攢绫诲瀷 - 0鏃犺惀閿€
    private Integer marketType = 0;

}
