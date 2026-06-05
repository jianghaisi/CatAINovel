package cn.bugstack.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 鍥炶皟浠诲姟瀹炰綋
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyTaskEntity {

    /**
     * 鎷煎崟缁勯槦ID
     */
    private String teamId;
    /**
     * 鍥炶皟绫诲瀷
     */
    private String notifyType;
    /**
     * 鍥炶皟娑堟伅
     */
    private String notifyMQ;
    /**
     * 鍥炶皟鎺ュ彛
     */
    private String notifyUrl;
    /**
     * 鍥炶皟娆℃暟
     */
    private Integer notifyCount;
    /**
     * 鍙傛暟瀵硅薄
     */
    private String parameterJson;
    /**
     * 鍞竴鏍囪瘑
     */
    private String uuid;

    public String lockKey() {
        return "notify_job_lock_key_" + this.uuid;
    }

}
