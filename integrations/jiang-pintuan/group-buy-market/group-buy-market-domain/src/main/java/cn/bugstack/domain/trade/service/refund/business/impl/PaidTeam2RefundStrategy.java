package cn.bugstack.domain.trade.service.refund.business.impl;

import cn.bugstack.domain.trade.model.aggregate.GroupBuyRefundAggregate;
import cn.bugstack.domain.trade.model.entity.GroupBuyTeamEntity;
import cn.bugstack.domain.trade.model.entity.NotifyTaskEntity;
import cn.bugstack.domain.trade.model.entity.TradeRefundOrderEntity;
import cn.bugstack.domain.trade.model.valobj.TeamRefundSuccess;
import cn.bugstack.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import cn.bugstack.domain.trade.service.refund.business.AbstractRefundOrderStrategy;
import cn.bugstack.types.enums.GroupBuyOrderEnumVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 鍙戣捣閫€鍗曪紙宸叉垚鍥?宸叉敮浠橈級锛岄攣鍗曢噺-1銆佸畬鎴愰噺-1銆佺粍闃熻鍗曠姸鎬佹洿鏂般€佸彂閫侀€€鍗曟秷鎭紙MQ锛?
 *
 * 2025/7/8 07:45
 */
@Slf4j
@Service("paidTeam2RefundStrategy")
public class PaidTeam2RefundStrategy extends AbstractRefundOrderStrategy {

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) {
        log.info("閫€鍗曪紱宸叉敮浠橈紝宸叉垚鍥?userId:{} teamId:{} orderId:{}", tradeRefundOrderEntity.getUserId(), tradeRefundOrderEntity.getTeamId(), tradeRefundOrderEntity.getOrderId());

        GroupBuyTeamEntity groupBuyTeamEntity = repository.queryGroupBuyTeamByTeamId(tradeRefundOrderEntity.getTeamId());
        Integer completeCount = groupBuyTeamEntity.getCompleteCount();

        // 鏈€鍚庝竴绗斾篃閫€鍗曪紝鍒欐洿鏂版嫾鍥㈣鍗曚负澶辫触
        GroupBuyOrderEnumVO groupBuyOrderEnumVO = 1 == completeCount ? GroupBuyOrderEnumVO.FAIL : GroupBuyOrderEnumVO.COMPLETE_FAIL;

        // 1. 閫€鍗曪紝宸叉敮浠?宸叉垚鍥?
        NotifyTaskEntity notifyTaskEntity = repository.paidTeam2Refund(GroupBuyRefundAggregate.buildPaidTeam2RefundAggregate(tradeRefundOrderEntity, -1, -1, groupBuyOrderEnumVO));

        // 2. 鍙戦€丮Q娑堟伅 - 鍙戦€丮Q锛屾仮澶嶉攣鍗曞簱瀛橀噺浣跨敤
        sendRefundNotifyMessage(notifyTaskEntity, "宸叉敮浠橈紝宸叉垚鍥?);

    }

    @Override
    public void reverseStock(TeamRefundSuccess teamRefundSuccess) throws Exception {
        log.info("閫€鍗曪紱宸叉敮浠樸€佸凡鎴愬洟锛岄槦浼嶇粍闃熺粨鏉燂紝涓嶉渶瑕佹仮澶嶉攣鍗曢噺 {} {} {}", teamRefundSuccess.getUserId(), teamRefundSuccess.getActivityId(), teamRefundSuccess.getTeamId());
    }

}
