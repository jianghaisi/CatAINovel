package cn.bugstack.trigger.job;

import cn.bugstack.domain.trade.service.ITradeSettlementOrderService;
import cn.bugstack.domain.trade.service.ITradeTaskService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description 鎷煎洟瀹岀粨鍥炶皟閫氱煡浠诲姟锛涙嫾鍥㈠洖璋冧换鍔¤〃锛屽疄闄呭叕鍙稿満鏅細瀹氭椂娓呯悊鏁版嵁缁撹浆锛屼笉浼氭湁澶鏁版嵁鎸ゅ帇
 */
@Slf4j
@Service
public class GroupBuyNotifyJob {

    @Resource
    private ITradeTaskService tradeTaskService;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0 0 0 * * ?")
    public void exec() {
        // 涓轰粈涔堝姞閿侊紵鍒嗗竷寮忓簲鐢∟鍙版満鍣ㄩ儴缃蹭簰澶囷紙涓€涓簲鐢ㄥ疄渚嬫寕浜嗭紝杩樻湁鍙﹀鍙敤鐨勶級锛屼换鍔¤皟搴︿細鏈塏涓悓鏃舵墽琛岋紝閭ｄ箞杩欓噷闇€瑕佸鍔犳姠鍗犳満鍒讹紝璋佹姠鍗犲埌璋佸氨鎵ц銆傚畬姣曞悗锛屼笅涓€杞户缁姠鍗犮€?
        RLock lock = redissonClient.getLock("group_buy_market_notify_job_exec");
        try {
            // waitTime锛氱瓑寰呰幏鍙栭攣鐨勬渶闀挎椂闂?
            // leaseTime锛氱绾︽椂闂达紝濡傛灉褰撳墠绾跨▼鎴愬姛鑾峰彇鍒伴攣锛岄偅涔堥攣灏嗚鎸佹湁鐨勬椂闂撮暱搴︺€傝繖涓椂闂磋繃鍚庯紝閿佷細鑷姩閲婃斁銆傜画绉熸椂闂村彲鎸夌収鎵ц鏂规硶鏃堕棿鐨勮€楁椂max鏉ヨ缃€傚 50姣
            boolean isLocked = lock.tryLock(3, 0, TimeUnit.SECONDS);
            if (!isLocked) return;

            Map<String, Integer> result = tradeTaskService.execNotifyJob();
            log.info("瀹氭椂浠诲姟锛屽洖璋冮€氱煡瀹屾垚 result:{}", JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("瀹氭椂浠诲姟锛屽洖璋冮€氱煡瀹屾垚澶辫触", e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}
