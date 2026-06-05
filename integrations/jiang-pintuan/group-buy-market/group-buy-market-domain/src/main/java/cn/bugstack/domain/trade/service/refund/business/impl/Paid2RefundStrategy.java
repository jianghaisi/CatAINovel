package cn.bugstack.domain.trade.service.refund.business.impl;

import cn.bugstack.domain.trade.model.aggregate.GroupBuyRefundAggregate;
import cn.bugstack.domain.trade.model.entity.NotifyTaskEntity;
import cn.bugstack.domain.trade.model.entity.TradeRefundOrderEntity;
import cn.bugstack.domain.trade.model.valobj.TeamRefundSuccess;
import cn.bugstack.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import cn.bugstack.domain.trade.service.refund.business.AbstractRefundOrderStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 鍙戣捣閫€鍗曪紙宸叉敮浠樸€佹湭鎴愬洟锛夛紝閿佸崟閲?1銆佸畬鎴愰噺-1銆佺粍闃熻鍗曠姸鎬佹洿鏂般€佸彂閫侀€€鍗曟秷鎭紙MQ锛?
 *
 * 2025/7/8 07:43
 */
@Slf4j
@Service("paid2RefundStrategy")
public class Paid2RefundStrategy extends AbstractRefundOrderStrategy {

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) throws Exception {
        log.info("閫€鍗曪紱宸叉敮浠橈紝鏈垚鍥?userId:{} teamId:{} orderId:{}", tradeRefundOrderEntity.getUserId(), tradeRefundOrderEntity.getTeamId(), tradeRefundOrderEntity.getOrderId());

        // 1. 閫€鍗曪紝宸叉敮浠?鏈垚鍥?
        NotifyTaskEntity notifyTaskEntity = repository.paid2Refund(GroupBuyRefundAggregate.buildPaid2RefundAggregate(tradeRefundOrderEntity, -1, -1));

        // 2. 鍙戦€丮Q娑堟伅 - 鍙戦€丮Q锛屾仮澶嶉攣鍗曞簱瀛橀噺浣跨敤
        sendRefundNotifyMessage(notifyTaskEntity, "宸叉敮浠橈紝鏈垚鍥?);
    }

    @Override
    public void reverseStock(TeamRefundSuccess teamRefundSuccess) throws Exception {
        doReverseStock(teamRefundSuccess, "宸叉敮浠橈紝鏈垚鍥紝浣嗘湁閿佸崟璁板綍锛岃鎭㈠閿佸崟搴撳瓨");
    }

}
