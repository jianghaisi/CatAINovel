package cn.bugstack.infrastructure.dcc;

import cn.bugstack.types.common.Constants;
import cn.bugstack.wrench.dynamic.config.center.types.annotations.DCCValue;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @description 鍔ㄦ€侀厤缃湇鍔?
 */
@Service
public class DCCService {

    /**
     * 闄嶇骇寮€鍏?0鍏抽棴銆?寮€鍚?
     */
    @DCCValue("downgradeSwitch:0")
    private String downgradeSwitch;

    @DCCValue("cutRange:100")
    private String cutRange;

    @DCCValue("scBlacklist:s02c02")
    private String scBlacklist;

    @DCCValue("cacheSwitch:0")
    private String cacheOpenSwitch;

    public boolean isDowngradeSwitch() {
        return "1".equals(downgradeSwitch);
    }

    public boolean isCutRange(String userId) {
        // 璁＄畻鍝堝笇鐮佺殑缁濆鍊?
        int hashCode = Math.abs(userId.hashCode());

        // 鑾峰彇鏈€鍚庝袱浣?
        int lastTwoDigits = hashCode % 100;

        // 鍒ゆ柇鏄惁鍦ㄥ垏閲忚寖鍥村唴
        if (lastTwoDigits <= Integer.parseInt(cutRange)) {
            return true;
        }

        return false;
    }

    /**
     * 鍒ゆ柇榛戝悕鍗曟嫤鎴笭閬擄紝true 鎷︽埅銆乫alse 鏀捐
     */
    public boolean isSCBlackIntercept(String source, String channel) {
        List<String> list = Arrays.asList(scBlacklist.split(Constants.SPLIT));
        return list.contains(source + channel);
    }

    /**
     * 缂撳瓨寮€鍚紑鍏筹紝true涓哄紑鍚紝1涓哄叧闂?
     */
    public boolean isCacheOpenSwitch(){
        return "0".equals(cacheOpenSwitch);
    }

}
