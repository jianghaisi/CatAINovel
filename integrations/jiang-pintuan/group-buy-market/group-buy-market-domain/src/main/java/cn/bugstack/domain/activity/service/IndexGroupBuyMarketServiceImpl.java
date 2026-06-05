package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.adapter.repository.IActivityRepository;
import cn.bugstack.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import cn.bugstack.domain.activity.model.entity.MarketProductEntity;
import cn.bugstack.domain.activity.model.entity.TrialBalanceEntity;
import cn.bugstack.domain.activity.model.valobj.TeamStatisticVO;
import cn.bugstack.domain.activity.service.trial.factory.DefaultActivityStrategyFactory;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @description 棣栭〉钀ラ攢鏈嶅姟
 */
@Service
public class IndexGroupBuyMarketServiceImpl implements IIndexGroupBuyMarketService {

    @Resource
    private DefaultActivityStrategyFactory defaultActivityStrategyFactory;
    @Resource
    private IActivityRepository repository;

    @Override
    public TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity) throws Exception {
        // 杩欓噷浣跨敤鈥滅瓥鐣ユ爲鈥濆鐞嗛椤佃瘯绠楋細鏍硅妭鐐规牎楠屻€佹椿鍔ㄦ煡璇€佷汉缇ゆ爣绛俱€佷紭鎯犺绠楃瓑鑺傜偣鎸夐『搴忔墽琛屻€?        StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactory.DynamicContext, TrialBalanceEntity> strategyHandler = defaultActivityStrategyFactory.strategyHandler();
        // DynamicContext 鐢ㄦ潵鍦ㄦ爲鑺傜偣涔嬮棿浼犻€掍腑闂存暟鎹紝閬垮厤姣忎釜鑺傜偣閲嶅鏌ヨ鏁版嵁搴撱€?        return strategyHandler.apply(marketProductEntity, new DefaultActivityStrategyFactory.DynamicContext());
    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailList(Long activityId, String userId, Integer ownerCount, Integer randomCount) {
        List<UserGroupBuyOrderDetailEntity> unionAllList = new ArrayList<>();

        // 鍏堟煡褰撳墠鐢ㄦ埛鑷繁鍙備笌鎴栧彂璧风殑闃熶紞锛屽墠绔€氬父浼氫紭鍏堝睍绀衡€滄垜鐨勫洟鈥濄€?        if (0 != ownerCount) {
            List<UserGroupBuyOrderDetailEntity> ownerList = repository.queryInProgressUserGroupBuyOrderDetailListByOwner(activityId, userId, ownerCount);
            if (null != ownerList && !ownerList.isEmpty()){
                unionAllList.addAll(ownerList);
            }
        }

        // 鍐嶆煡鍏朵粬鐢ㄦ埛鐨勮繘琛屼腑闃熶紞锛岀敤浜庘€滃幓鍙傚洟鈥濈殑鎺ㄨ崘灞曠ず銆?        if (0 != randomCount) {
            List<UserGroupBuyOrderDetailEntity> randomList = repository.queryInProgressUserGroupBuyOrderDetailListByRandom(activityId, userId, randomCount);
            if (null != randomList && !randomList.isEmpty()){
                unionAllList.addAll(randomList);
            }
        }

        return unionAllList;
    }

    @Override
    public TeamStatisticVO queryTeamStatisticByActivityId(Long activityId) {
        return repository.queryTeamStatisticByActivityId(activityId);
    }

}
