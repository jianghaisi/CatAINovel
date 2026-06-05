package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import cn.bugstack.domain.activity.model.entity.MarketProductEntity;
import cn.bugstack.domain.activity.model.entity.TrialBalanceEntity;
import cn.bugstack.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

/**
 * @description 棣栭〉钀ラ攢鏈嶅姟鎺ュ彛
 */
public interface IIndexGroupBuyMarketService {

    TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity) throws Exception;

    /**
     * 鏌ヨ杩涜涓殑鎷煎洟璁㈠崟
     *
     * @param activityId  娲诲姩ID
     * @param userId      鐢ㄦ埛ID
     * @param ownerCount  涓汉鏁伴噺
     * @param randomCount 闅忔満鏁伴噺
     * @return 鐢ㄦ埛鎷煎洟鏄庣粏鏁版嵁
     */
    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailList(Long activityId, String userId, Integer ownerCount, Integer randomCount);

    /**
     * 娲诲姩鎷煎洟闃熶紞鎬荤粨
     *
     * @param activityId 娲诲姩ID
     * @return 闃熶紞缁熻
     */
    TeamStatisticVO queryTeamStatisticByActivityId(Long activityId);

}
