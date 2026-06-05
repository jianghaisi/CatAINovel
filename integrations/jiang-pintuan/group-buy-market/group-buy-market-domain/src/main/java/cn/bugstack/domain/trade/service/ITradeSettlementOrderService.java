package cn.bugstack.domain.trade.service;

import cn.bugstack.domain.trade.model.entity.NotifyTaskEntity;
import cn.bugstack.domain.trade.model.entity.TradePaySettlementEntity;
import cn.bugstack.domain.trade.model.entity.TradePaySuccessEntity;

import java.util.Map;

/**
 * @description 鎷煎洟浜ゆ槗缁撶畻鏈嶅姟鎺ュ彛
 */
public interface ITradeSettlementOrderService {

    /**
     * 钀ラ攢缁撶畻
     *
     * @param tradePaySuccessEntity 浜ゆ槗鏀粯璁㈠崟瀹炰綋瀵硅薄
     * @return 浜ゆ槗缁撶畻璁㈠崟瀹炰綋
     */
    TradePaySettlementEntity settlementMarketPayOrder(TradePaySuccessEntity tradePaySuccessEntity) throws Exception;

}
