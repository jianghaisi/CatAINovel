package cn.bugstack.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 钀ラ攢鍟嗗搧瀹炰綋淇℃伅锛岄€氳繃杩欐牱涓€涓俊鎭幏鍙栧晢鍝佷紭鎯犱俊鎭?
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketProductEntity {

    /** 娲诲姩ID */
    private Long activityId;
    /** 鐢ㄦ埛ID */
    private String userId;
    /** 鍟嗗搧ID */
    private String goodsId;
    /** 鏉ユ簮 */
    private String source;
    /** 娓犻亾 */
    private String channel;

}
