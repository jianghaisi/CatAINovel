package cn.bugstack.domain.trade.service;

import cn.bugstack.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import cn.bugstack.domain.trade.model.entity.TradeRefundBehaviorEntity;
import cn.bugstack.domain.trade.model.entity.TradeRefundCommandEntity;
import cn.bugstack.domain.trade.model.valobj.TeamRefundSuccess;

import java.util.List;

/**
 * 閫€鍗曪紝閫嗗悜娴佺▼鎺ュ彛
 *
 * 2025/7/8 07:24
 */
public interface ITradeRefundOrderService {

    TradeRefundBehaviorEntity refundOrder(TradeRefundCommandEntity tradeRefundCommandEntity) throws Exception;

    /**
     * 閫€鍗曟仮澶嶉攣鍗曞簱瀛?
     * @param teamRefundSuccess 閫€鍗曟秷鎭?
     * @throws Exception 寮傚父
     */
    void restoreTeamLockStock(TeamRefundSuccess teamRefundSuccess) throws Exception;

    /**
     * 鏌ヨ瓒呮椂鏈敮浠樿鍗曞垪琛?
     * 鏉′欢锛氬綋鍓嶆椂闂翠笉鍦ㄦ椿鍔ㄦ椂闂磋寖鍥村唴銆佺姸鎬佷负0锛堝垵濮嬮攣瀹氾級銆乷ut_trade_time涓虹┖
     * @return 瓒呮椂鏈敮浠樿鍗曞垪琛紝闄愬埗10鏉?
     */
    List<UserGroupBuyOrderDetailEntity> queryTimeoutUnpaidOrderList();

}
