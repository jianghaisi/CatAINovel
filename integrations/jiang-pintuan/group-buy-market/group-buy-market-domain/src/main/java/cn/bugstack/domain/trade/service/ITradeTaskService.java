package cn.bugstack.domain.trade.service;

import cn.bugstack.domain.trade.model.entity.NotifyTaskEntity;

import java.util.Map;

/**
 * 浜ゆ槗浠诲姟锛圡T/HTTP锛夋湇鍔℃帴鍙?
 * 
 * 2025/7/12 21:15
 */
public interface ITradeTaskService {

    /**
     * 鎵ц缁撶畻閫氱煡浠诲姟
     *
     * @return 缁撶畻鏁伴噺
     * @throws Exception 寮傚父
     */
    Map<String, Integer> execNotifyJob() throws Exception;

    /**
     * 鎵ц缁撶畻閫氱煡浠诲姟
     *
     * @param teamId 鎸囧畾缁撶畻缁処D
     * @return 缁撶畻鏁伴噺
     * @throws Exception 寮傚父
     */
    Map<String, Integer> execNotifyJob(String teamId) throws Exception;

    /**
     * 鎵ц缁撶畻閫氱煡浠诲姟
     *
     * @param notifyTaskEntity 閫氱煡浠诲姟瀵硅薄
     * @return 缁撶畻鏁伴噺
     * @throws Exception 寮傚父
     */
    Map<String, Integer> execNotifyJob(NotifyTaskEntity notifyTaskEntity) throws Exception;

}
