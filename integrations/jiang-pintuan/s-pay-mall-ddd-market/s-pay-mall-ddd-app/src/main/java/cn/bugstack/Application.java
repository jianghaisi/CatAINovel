package cn.bugstack;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@Configurable
@EnableScheduling
public class Application {

    // 鍟嗗煄鏀粯鏈嶅姟鍚姩鍏ュ彛銆傚畠璐熻矗鏈湴鍟嗗搧璁㈠崟銆佹敮浠樺疂棰勬敮浠樸€佹敮浠樺洖璋冿紝浠ュ強璋冪敤鎷煎洟钀ラ攢鏈嶅姟銆?    public static void main(String[] args){
        SpringApplication.run(Application.class);
    }

}
