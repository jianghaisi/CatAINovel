package cn.bugstack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread.pool.executor.config", ignoreInvalidFields = true)
public class ThreadPoolConfigProperties {

    /** 鏍稿績绾跨▼鏁?*/
    private Integer corePoolSize = 20;
    /** 鏈€澶х嚎绋嬫暟 */
    private Integer maxPoolSize = 200;
    /** 鏈€澶х瓑寰呮椂闂?*/
    private Long keepAliveTime = 10L;
    /** 鏈€澶ч槦鍒楁暟 */
    private Integer blockQueueSize = 5000;
    /*
     * AbortPolicy锛氫涪寮冧换鍔″苟鎶涘嚭RejectedExecutionException寮傚父銆?
     * DiscardPolicy锛氱洿鎺ヤ涪寮冧换鍔★紝浣嗘槸涓嶄細鎶涘嚭寮傚父
     * DiscardOldestPolicy锛氬皢鏈€鏃╄繘鍏ラ槦鍒楃殑浠诲姟鍒犻櫎锛屼箣鍚庡啀灏濊瘯鍔犲叆闃熷垪鐨勪换鍔¤鎷掔粷
     * CallerRunsPolicy锛氬鏋滀换鍔℃坊鍔犵嚎绋嬫睜澶辫触锛岄偅涔堜富绾跨▼鑷繁鎵ц璇ヤ换鍔?
     * */
    private String policy = "AbortPolicy";

}
