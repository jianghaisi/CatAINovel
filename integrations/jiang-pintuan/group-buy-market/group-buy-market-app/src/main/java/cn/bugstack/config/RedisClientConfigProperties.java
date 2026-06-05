package cn.bugstack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description Redis 杩炴帴閰嶇疆 <a href="https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter">redisson-spring-boot-starter</a>
 */
@Data
@ConfigurationProperties(prefix = "redis.sdk.config", ignoreInvalidFields = true)
public class RedisClientConfigProperties {

    /** host:ip */
    private String host;
    /** 绔彛 */
    private int port;
    /** 璐﹀瘑 */
    private String password;
    /** 璁剧疆杩炴帴姹犵殑澶у皬锛岄粯璁や负64 */
    private int poolSize = 64;
    /** 璁剧疆杩炴帴姹犵殑鏈€灏忕┖闂茶繛鎺ユ暟锛岄粯璁や负10 */
    private int minIdleSize = 10;
    /** 璁剧疆杩炴帴鐨勬渶澶х┖闂叉椂闂达紙鍗曚綅锛氭绉掞級锛岃秴杩囪鏃堕棿鐨勭┖闂茶繛鎺ュ皢琚叧闂紝榛樿涓?0000 */
    private int idleTimeout = 10000;
    /** 璁剧疆杩炴帴瓒呮椂鏃堕棿锛堝崟浣嶏細姣锛夛紝榛樿涓?0000 */
    private int connectTimeout = 10000;
    /** 璁剧疆杩炴帴閲嶈瘯娆℃暟锛岄粯璁や负3 */
    private int retryAttempts = 3;
    /** 璁剧疆杩炴帴閲嶈瘯鐨勯棿闅旀椂闂达紙鍗曚綅锛氭绉掞級锛岄粯璁や负1000 */
    private int retryInterval = 1000;
    /** 璁剧疆瀹氭湡妫€鏌ヨ繛鎺ユ槸鍚﹀彲鐢ㄧ殑鏃堕棿闂撮殧锛堝崟浣嶏細姣锛夛紝榛樿涓?锛岃〃绀轰笉杩涜瀹氭湡妫€鏌?*/
    private int pingInterval = 0;
    /** 璁剧疆鏄惁淇濇寔闀胯繛鎺ワ紝榛樿涓簍rue */
    private boolean keepAlive = true;

}
