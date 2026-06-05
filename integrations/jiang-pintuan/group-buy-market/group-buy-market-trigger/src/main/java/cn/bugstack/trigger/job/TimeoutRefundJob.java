package cn.bugstack.trigger.job;

import cn.bugstack.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import cn.bugstack.domain.trade.model.entity.TradeRefundCommandEntity;
import cn.bugstack.domain.trade.service.ITradeRefundOrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @description 瓒呮椂鏈敮浠樿鍗曢€€鍗曞畾鏃朵换鍔?
 */
@Slf4j
@Service
public class TimeoutRefundJob {

    @Resource
    private ITradeRefundOrderService tradeRefundOrderService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 姣?鍒嗛挓鎵ц涓€娆¤秴鏃惰鍗曟壂鎻?
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void exec() {
        // 鍒嗗竷寮忛攣锛岄槻姝㈠瀹炰緥閲嶅鎵ц
        RLock lock = redissonClient.getLock("group_buy_market_timeout_refund_job_exec");
        try {
            // waitTime锛氱瓑寰呰幏鍙栭攣鐨勬渶闀挎椂闂?
            // leaseTime锛氱绾︽椂闂达紝閿佺殑鎸佹湁鏃堕棿
            boolean isLocked = lock.tryLock(3, 60, TimeUnit.SECONDS);
            if (!isLocked) {
                log.info("瓒呮椂閫€鍗曞畾鏃朵换鍔★紝鑾峰彇閿佸け璐ワ紝璺宠繃鏈鎵ц");
                return;
            }

            log.info("瓒呮椂閫€鍗曞畾鏃朵换鍔″紑濮嬫墽琛?);
            
            // 鏌ヨ瓒呮椂鏈敮浠樿鍗曞垪琛?
            List<UserGroupBuyOrderDetailEntity> timeoutOrderList = tradeRefundOrderService.queryTimeoutUnpaidOrderList();
            if (timeoutOrderList == null || timeoutOrderList.isEmpty()) {
                log.info("瓒呮椂閫€鍗曞畾鏃朵换鍔★紝鏈彂鐜拌秴鏃舵湭鏀粯璁㈠崟");
                return;
            }

            log.info("瓒呮椂閫€鍗曞畾鏃朵换鍔★紝鍙戠幇瓒呮椂鏈敮浠樿鍗曟暟閲忥細{}", timeoutOrderList.size());
            
            int successCount = 0;
            int failCount = 0;
            
            // 閬嶅巻澶勭悊姣忎釜瓒呮椂璁㈠崟
            for (UserGroupBuyOrderDetailEntity orderDetail : timeoutOrderList) {
                try {
                    // 鏋勫缓閫€鍗曞懡浠?
                    TradeRefundCommandEntity refundCommand = TradeRefundCommandEntity.builder()
                            .userId(orderDetail.getUserId())
                            .outTradeNo(orderDetail.getOutTradeNo())
                            .source(orderDetail.getSource())
                            .channel(orderDetail.getChannel())
                            .build();
                    
                    // 鎵ц閫€鍗?
                    tradeRefundOrderService.refundOrder(refundCommand);
                    successCount++;
                    
                    log.info("瓒呮椂璁㈠崟閫€鍗曟垚鍔燂紝鐢ㄦ埛ID锛歿}锛屼氦鏄撳崟鍙凤細{}", orderDetail.getUserId(), orderDetail.getOutTradeNo());
                    
                } catch (Exception e) {
                    failCount++;
                    log.error("瓒呮椂璁㈠崟閫€鍗曞け璐ワ紝鐢ㄦ埛ID锛歿}锛屼氦鏄撳崟鍙凤細{}锛岄敊璇俊鎭細{}", 
                            orderDetail.getUserId(), orderDetail.getOutTradeNo(), e.getMessage(), e);
                }
            }
            
            log.info("瓒呮椂閫€鍗曞畾鏃朵换鍔℃墽琛屽畬鎴愶紝鎴愬姛锛歿}锛屽け璐ワ細{}", successCount, failCount);
            
        } catch (Exception e) {
            log.error("瓒呮椂閫€鍗曞畾鏃朵换鍔℃墽琛屽紓甯?, e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}