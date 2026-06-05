package cn.bugstack.trigger.job;

import cn.bugstack.domain.order.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description 瓒呮椂鍏冲崟
 */
@Slf4j
@Component()
public class TimeoutCloseOrderJob {

    @Resource
    private IOrderService orderService;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void exec() {
        try {
            log.info("浠诲姟锛涜秴鏃?0鍒嗛挓璁㈠崟鍏抽棴");
            List<String> orderIds = orderService.queryTimeoutCloseOrderList();
            if (null == orderIds || orderIds.isEmpty()) {
                log.info("瀹氭椂浠诲姟锛岃秴鏃?0鍒嗛挓璁㈠崟鍏抽棴锛屾殏鏃犺秴鏃舵湭鏀粯璁㈠崟 orderIds is null");
                return;
            }
            for (String orderId : orderIds) {
                boolean status = orderService.changeOrderClose(orderId);
                log.info("瀹氭椂浠诲姟锛岃秴鏃?0鍒嗛挓璁㈠崟鍏抽棴 orderId: {} status锛歿}", orderId, status);
            }
        } catch (Exception e) {
            log.error("瀹氭椂浠诲姟锛岃秴鏃?5鍒嗛挓璁㈠崟鍏抽棴澶辫触", e);
        }
    }

}
