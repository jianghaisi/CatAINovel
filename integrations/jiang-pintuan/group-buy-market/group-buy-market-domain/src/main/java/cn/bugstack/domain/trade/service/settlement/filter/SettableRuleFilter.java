package cn.bugstack.domain.trade.service.settlement.filter;

import cn.bugstack.domain.trade.adapter.repository.ITradeRepository;
import cn.bugstack.domain.trade.model.entity.GroupBuyTeamEntity;
import cn.bugstack.domain.trade.model.entity.MarketPayOrderEntity;
import cn.bugstack.domain.trade.model.entity.TradeSettlementRuleCommandEntity;
import cn.bugstack.domain.trade.model.entity.TradeSettlementRuleFilterBackEntity;
import cn.bugstack.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.wrench.design.framework.link.model2.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @description 鍙粨绠楄鍒欒繃婊わ紱浜ゆ槗鏃堕棿
 */
@Slf4j
@Service
public class SettableRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity> {

    @Resource
    private ITradeRepository repository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        log.info("缁撶畻瑙勫垯杩囨护-鏈夋晥鏃堕棿鏍￠獙{} outTradeNo:{}", requestParameter.getUserId(), requestParameter.getOutTradeNo());

        // 涓婁笅鏂囷紱鑾峰彇鏁版嵁
        MarketPayOrderEntity marketPayOrderEntity = dynamicContext.getMarketPayOrderEntity();

        // 鏌ヨ鎷煎洟瀵硅薄
        GroupBuyTeamEntity groupBuyTeamEntity = repository.queryGroupBuyTeamByTeamId(marketPayOrderEntity.getTeamId());

        // 澶栭儴浜ゆ槗鏃堕棿 - 涔熷氨鏄敤鎴锋敮浠樺畬鎴愮殑鏃堕棿锛岃繖涓椂闂磋鍦ㄦ嫾鍥㈡湁鏁堟椂闂磋寖鍥村唴
        Date outTradeTime = requestParameter.getOutTradeTime();

        // 鍒ゆ柇锛屽閮ㄤ氦鏄撴椂闂达紝瑕佸皬浜庢嫾鍥㈢粨鏉熸椂闂淬€傚惁鍒欐姏寮傚父銆?
        if (!outTradeTime.before(groupBuyTeamEntity.getValidEndTime())) {
            log.error("璁㈠崟浜ゆ槗鏃堕棿涓嶅湪鎷煎洟鏈夋晥鏃堕棿鑼冨洿鍐?);
            throw new AppException(ResponseCode.E0106);
        }

        // 璁剧疆涓婁笅鏂?
        dynamicContext.setGroupBuyTeamEntity(groupBuyTeamEntity);

        return next(requestParameter, dynamicContext);
    }

}
