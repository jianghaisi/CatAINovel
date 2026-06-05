package cn.bugstack.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @description 鍥炶皟閰嶇疆鍊煎璞?
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyConfigVO {

    /**
     * 鍥炶皟鏂瑰紡锛汳Q銆丠TTP
     */
    private NotifyTypeEnumVO notifyType;
    /**
     * 鍥炶皟娑堟伅
     */
    private String notifyMQ;
    /**
     * 鍥炶皟鍦板潃
     */
    private String notifyUrl;

}
