package cn.bugstack.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @description 閫氱煡鍥炶皟浠诲姟
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyTask {

    /** 鑷ID */
    private Long id;
    /** 娲诲姩ID */
    private Long activityId;
    /** 鎷煎崟缁勯槦ID */
    private String teamId;
    /** 鍥炶皟绉嶇被 */
    private String notifyCategory;
    /** 鍥炶皟绫诲瀷 */
    private String notifyType;
    /** 鍥炶皟娑堟伅 */
    private String notifyMQ;
    /** 鍥炶皟鎺ュ彛 */
    private String notifyUrl;
    /** 鍥炶皟娆℃暟 */
    private Integer notifyCount;
    /** 鍥炶皟鐘舵€併€?鍒濆銆?瀹屾垚銆?閲嶈瘯銆?澶辫触銆?*/
    private Integer notifyStatus;
    /** 鍙傛暟瀵硅薄 */
    private String parameterJson;
    /** 鍞竴鏍囪瘑 */
    private String uuid;
    /** 鍒涘缓鏃堕棿 */
    private Date createTime;
    /** 鏇存柊鏃堕棿 */
    private Date updateTime;

}
