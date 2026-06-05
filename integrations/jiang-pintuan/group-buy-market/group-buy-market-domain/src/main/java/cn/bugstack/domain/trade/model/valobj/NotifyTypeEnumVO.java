package cn.bugstack.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 鍥炶皟鏂瑰紡鏋氫妇
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NotifyTypeEnumVO {

    HTTP("HTTP", "HTTP 鍥炶皟"),
    MQ("MQ", "MQ 娑堟伅閫氱煡"),
    ;

    private String code;
    private String info;

}
