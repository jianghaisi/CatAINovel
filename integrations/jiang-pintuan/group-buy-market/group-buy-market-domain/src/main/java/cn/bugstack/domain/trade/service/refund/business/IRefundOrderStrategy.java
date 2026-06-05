package cn.bugstack.domain.trade.service.refund.business;

import cn.bugstack.domain.trade.model.entity.TradeRefundOrderEntity;
import cn.bugstack.domain.trade.model.valobj.TeamRefundSuccess;

/**
 * 閫€鍗曠瓥鐣ユ帴鍙?
 * 鏈敮浠橈紝Unpaid
 * 鏈垚鍥紝UnformedTeam
 * 宸叉垚鍥紝AlreadyFormedTeam
 *
 * 2025/7/8 07:37
 */
public interface IRefundOrderStrategy {

    void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) throws Exception;

    void reverseStock(TeamRefundSuccess teamRefundSuccess) throws Exception;

}
