package cn.bugstack.domain.activity.service.trial.thread;

import cn.bugstack.domain.activity.adapter.repository.IActivityRepository;
import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.model.valobj.SCSkuActivityVO;

import java.util.concurrent.Callable;

/**
 * @description 鏌ヨ钀ラ攢閰嶇疆浠诲姟
 */
public class QueryGroupBuyActivityDiscountVOThreadTask implements Callable<GroupBuyActivityDiscountVO> {

    /**
     * 娲诲姩ID
     */
    private final Long activityId;

    /**
     * 鏉ユ簮
     */
    private final String source;

    /**
     * 娓犻亾
     */
    private final String channel;

    /**
     * 鍟嗗搧ID
     */
    private final String goodsId;

    /**
     * 娲诲姩浠撳偍
     */
    private final IActivityRepository activityRepository;

    public QueryGroupBuyActivityDiscountVOThreadTask(Long activityId, String source, String channel, String goodsId, IActivityRepository activityRepository) {
        this.activityId = activityId;
        this.source = source;
        this.channel = channel;
        this.goodsId = goodsId;
        this.activityRepository = activityRepository;
    }

    @Override
    public GroupBuyActivityDiscountVO call() throws Exception {
        // 鍒ゆ柇鏄惁瀛樺湪鍙敤鐨勬椿鍔↖D
        Long availableActivityId = activityId;
        if (null == activityId){
            // 鏌ヨ娓犻亾鍟嗗搧娲诲姩閰嶇疆鍏宠仈閰嶇疆
            SCSkuActivityVO scSkuActivityVO = activityRepository.querySCSkuActivityBySCGoodsId(source, channel, goodsId);
            if (null == scSkuActivityVO) return null;
            availableActivityId = scSkuActivityVO.getActivityId();
        }
        // 鏌ヨ娲诲姩閰嶇疆
        return activityRepository.queryGroupBuyActivityDiscountVO(availableActivityId);
    }

}
