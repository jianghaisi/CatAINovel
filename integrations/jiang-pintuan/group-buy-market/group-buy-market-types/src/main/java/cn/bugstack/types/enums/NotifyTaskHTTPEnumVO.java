package cn.bugstack.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 鍥炶皟浠诲姟鐘舵€?
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NotifyTaskHTTPEnumVO {

    SUCCESS("success", "鎴愬姛"),
    ERROR("error", "澶辫触"),
    NULL(null, "绌烘墽琛?),
    ;

    private String code;
    private String info;

}
