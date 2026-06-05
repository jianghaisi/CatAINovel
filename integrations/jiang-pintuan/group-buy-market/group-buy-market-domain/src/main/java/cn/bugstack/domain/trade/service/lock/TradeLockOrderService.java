package cn.bugstack.domain.trade.service.lock;

import cn.bugstack.domain.trade.adapter.repository.ITradeRepository;
import cn.bugstack.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import cn.bugstack.domain.trade.model.entity.*;
import cn.bugstack.domain.trade.model.valobj.GroupBuyProgressVO;
import cn.bugstack.domain.trade.service.ITradeLockOrderService;
import cn.bugstack.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import cn.bugstack.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @description 浜ゆ槗璁㈠崟鏈嶅姟
 */
@Slf4j
@Service
public class TradeLockOrderService implements ITradeLockOrderService {

    @Resource
    private ITradeRepository repository;
    @Resource
    private BusinessLinkedList<TradeLockRuleCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockRuleFilterBackEntity> tradeRuleFilter;

    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outTradeNo) {
        log.info("鎷煎洟浜ゆ槗-鏌ヨ鏈敮浠樿惀閿€璁㈠崟:{} outTradeNo:{}", userId, outTradeNo);
        return repository.queryMarketPayOrderEntityByOutTradeNo(userId, outTradeNo);
    }

    @Override
    public GroupBuyProgressVO queryGroupBuyProgress(String teamId) {
        log.info("鎷煎洟浜ゆ槗-鏌ヨ鎷煎崟杩涘害:{}", teamId);
        return repository.queryGroupBuyProgress(teamId);
    }

    @Override
    public MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) throws Exception {
        log.info("鎷煎洟浜ゆ槗-閿佸畾钀ラ攢浼樻儬鏀粯璁㈠崟:{} activityId:{} goodsId:{}", userEntity.getUserId(), payActivityEntity.getActivityId(), payDiscountEntity.getGoodsId());

        // 瑙勫垯閾捐礋璐ｉ攣鍗曞墠鐨勫叧閿牎楠岋細娲诲姩鍙敤鎬с€佺敤鎴峰弬涓庢鏁伴檺鍒躲€丷edis 闃熶紞搴撳瓨鍗犵敤绛夈€?        TradeLockRuleFilterBackEntity tradeLockRuleFilterBackEntity = tradeRuleFilter.apply(TradeLockRuleCommandEntity.builder()
                        .activityId(payActivityEntity.getActivityId())
                        .userId(userEntity.getUserId())
                        .teamId(payActivityEntity.getTeamId())
                        .build(),
                new TradeLockRuleFilterFactory.DynamicContext());

        // 鐢ㄦ埛宸插弬涓庢鏁颁細鍙備笌 bizId 鐢熸垚锛岀敤鏁版嵁搴撳敮涓€绾︽潫鍏滃簳闃叉瓒呰繃娲诲姩鍙備笌涓婇檺銆?        Integer userTakeOrderCount = tradeLockRuleFilterBackEntity.getUserTakeOrderCount();

        // 鑱氬悎瀵硅薄鎶娾€滅敤鎴枫€佹椿鍔ㄣ€佷环鏍间紭鎯犮€佸弬涓庢鏁扳€濆悎鍦ㄤ竴璧凤紝浜ょ粰浠撳偍涓€娆℃€ц惤搴撱€?        GroupBuyOrderAggregate groupBuyOrderAggregate = GroupBuyOrderAggregate.builder()
                .userEntity(userEntity)
                .payActivityEntity(payActivityEntity)
                .payDiscountEntity(payDiscountEntity)
                .userTakeOrderCount(userTakeOrderCount)
                .build();

        try {
            // 閿佸崟鍙〃绀衡€滃崰浣忎竴涓嫾鍥㈠悕棰濃€濓紝姝ゆ椂鐢ㄦ埛杩樻病鏀粯锛涘悗缁細璧版敮浠樻垚鍔熺粨绠楁垨瓒呮椂閫€鍗曘€?            return repository.lockMarketPayOrder(groupBuyOrderAggregate);
        } catch (Exception e) {
            // 濡傛灉鏁版嵁搴撹惤搴撳け璐ワ紝瑕佹妸鍓嶉潰 Redis 鍗犵敤鐨勫悕棰濊褰曚负鍙仮澶嶏紝閬垮厤搴撳瓨琚櫧鐧藉崰浣忋€?            repository.recoveryTeamStock(tradeLockRuleFilterBackEntity.getRecoveryTeamStockKey(), payActivityEntity.getValidTime());
            throw e;
        }

    }

}
