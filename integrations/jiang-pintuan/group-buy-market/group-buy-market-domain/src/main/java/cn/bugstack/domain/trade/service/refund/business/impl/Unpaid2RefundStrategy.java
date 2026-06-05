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
 * 鏈敮浠橈紝鏈垚鍥紱鍙戣捣閫€鍗曪紙鏈敮浠橈級锛岄攣鍗曢噺-1銆佺粍闃熻鍗曠姸鎬佹洿鏂?
 *
 * 2025/7/8 07:41
 */
@Slf4j
@Service("unpaid2RefundStrategy")
public class Unpaid2RefundStrategy extends AbstractRefundOrderStrategy {

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) {
        log.info("閫€鍗曪紱鏈敮浠橈紝鏈垚鍥?userId:{} teamId:{} orderId:{}", tradeRefundOrderEntity.getUserId(), tradeRefundOrderEntity.getTeamId(), tradeRefundOrderEntity.getOrderId());
        // 1. 閫€鍗曪紱鏈敮浠橈紝鏈垚鍥?
        NotifyTaskEntity notifyTaskEntity = repository.unpaid2Refund(GroupBuyRefundAggregate.buildUnpaid2RefundAggregate(tradeRefundOrderEntity, -1));

        // 2. 鍙戦€丮Q娑堟伅 - 鍙戦€丮Q锛屾仮澶嶉攣鍗曞簱瀛橀噺浣跨敤
        sendRefundNotifyMessage(notifyTaskEntity, "鏈敮浠橈紝鏈垚鍥?);
    }

    @Override
    public void reverseStock(TeamRefundSuccess teamRefundSuccess) throws Exception {
        doReverseStock(teamRefundSuccess, "鏈敮浠橈紝鏈垚鍥紝浣嗘湁閿佸崟璁板綍锛岃鎭㈠閿佸崟搴撳瓨");
    }

}
