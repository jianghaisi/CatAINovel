package cn.bugstack;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configurable
@EnableScheduling
public class Application {

    // 鎷煎洟钀ラ攢鏈嶅姟鐨勫惎鍔ㄥ叆鍙ｃ€係pring Boot 浼氫粠杩欓噷寮€濮嬫壂鎻?Controller銆丼ervice銆丷epository 绛夌粍浠躲€?
    // @EnableScheduling 琛ㄧず鍚敤瀹氭椂浠诲姟锛屼緥濡傝秴鏃舵湭鏀粯閫€鍗曘€侀€氱煡浠诲姟閲嶈瘯绛夊悗鍙拌ˉ鍋挎祦绋嬨€?
    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
