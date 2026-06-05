package cn.bugstack.trigger.http;

import cn.bugstack.api.dto.NotifyRequestDTO;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @description 鍥炶皟鏈嶅姟鎺ュ彛娴嬭瘯
 */
@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/test/")
public class TestApiClientController {

    /**
     * 妯℃嫙鍥炶皟妗堜緥
     *
     * @param notifyRequestDTO 閫氱煡鍥炶皟鍙傛暟
     * @return success 鎴愬姛锛宔rror 澶辫触
     */
    @RequestMapping(value = "group_buy_notify", method = RequestMethod.POST)
    public String groupBuyNotify(@RequestBody NotifyRequestDTO notifyRequestDTO) {
        log.info("妯℃嫙娴嬭瘯绗笁鏂规湇鍔℃帴鏀舵嫾鍥㈠洖璋?{}", JSON.toJSONString(notifyRequestDTO));

        return "success";
    }

}
