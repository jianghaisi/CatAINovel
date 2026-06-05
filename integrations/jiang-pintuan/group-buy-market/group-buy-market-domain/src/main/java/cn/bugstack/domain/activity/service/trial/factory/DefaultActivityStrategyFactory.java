package cn.bugstack.domain.activity.service.trial.factory;

import cn.bugstack.domain.activity.model.entity.MarketProductEntity;
import cn.bugstack.domain.activity.model.entity.TrialBalanceEntity;
import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.model.valobj.SkuVO;
import cn.bugstack.domain.activity.service.trial.node.RootNode;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description 娲诲姩绛栫暐宸ュ巶
 */
@Service
public class DefaultActivityStrategyFactory {

    private final RootNode rootNode;

    public DefaultActivityStrategyFactory(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    public StrategyHandler<MarketProductEntity, DynamicContext, TrialBalanceEntity> strategyHandler() {
        return rootNode;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {
        // 鎷煎洟娲诲姩钀ラ攢閰嶇疆鍊煎璞?
        private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;
        // 鍟嗗搧淇℃伅
        private SkuVO skuVO;
        // 鎶樻墸閲戦
        private BigDecimal deductionPrice;
        // 鏀粯閲戦
        private BigDecimal payPrice;
        // 娲诲姩鍙鎬ч檺鍒?
        private boolean visible;
        // 娲诲姩
        private boolean enable;
    }

}
