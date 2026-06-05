package cn.bugstack.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alipay", ignoreInvalidFields = true)
public class AliPayConfigProperties {

    // 銆屾矙绠辩幆澧冦€嶅簲鐢↖D - 鎮ㄧ殑APPID锛屾敹娆捐处鍙锋棦鏄綘鐨凙PPID瀵瑰簲鏀粯瀹濊处鍙枫€傝幏鍙栧湴鍧€锛沨ttps://open.alipay.com/develop/sandbox/app
    private String app_id;
    // 銆屾矙绠辩幆澧冦€嶅晢鎴风閽ワ紝浣犵殑PKCS8鏍煎紡RSA2绉侀挜
    private String merchant_private_key;
    // 銆屾矙绠辩幆澧冦€嶆敮浠樺疂鍏挜
    private String alipay_public_key;
    // 銆屾矙绠辩幆澧冦€嶆湇鍔″櫒寮傛閫氱煡椤甸潰璺緞
    private String notify_url;
    // 銆屾矙绠辩幆澧冦€嶉〉闈㈣烦杞悓姝ラ€氱煡椤甸潰璺緞 闇€http://鏍煎紡鐨勫畬鏁磋矾寰勶紝涓嶈兘鍔?id=123杩欑被鑷畾涔夊弬鏁帮紝蹇呴』澶栫綉鍙互姝ｅ父璁块棶
    private String return_url;
    // 銆屾矙绠辩幆澧冦€?
    private String gatewayUrl;
    // 绛惧悕鏂瑰紡
    private String sign_type = "RSA2";
    // 瀛楃缂栫爜鏍煎紡
    private String charset = "utf-8";
    // 浼犺緭鏍煎紡
    private String format = "json";

}
