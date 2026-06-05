package cn.bugstack.api;

import cn.bugstack.api.dto.LockMarketPayOrderRequestDTO;
import cn.bugstack.api.dto.LockMarketPayOrderResponseDTO;
import cn.bugstack.api.dto.RefundMarketPayOrderRequestDTO;
import cn.bugstack.api.dto.RefundMarketPayOrderResponseDTO;
import cn.bugstack.api.dto.SettlementMarketPayOrderRequestDTO;
import cn.bugstack.api.dto.SettlementMarketPayOrderResponseDTO;
import cn.bugstack.api.response.Response;

/**
 * @description 钀ラ攢浜ゆ槗鏈嶅姟鎺ュ彛
 */
public interface IMarketTradeService {

    /**
     * 钀ラ攢閿佸崟
     *
     * @param requestDTO 閿佸崟鍟嗗搧淇℃伅
     * @return 閿佸崟缁撴灉淇℃伅
     */
    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO);

    /**
     * 钀ラ攢缁撶畻
     *
     * @param requestDTO 缁撶畻鍟嗗搧淇℃伅
     * @return 缁撶畻缁撴灉淇℃伅
     */
    Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(SettlementMarketPayOrderRequestDTO requestDTO);

    /**
     * 钀ラ攢鎷煎洟閫€鍗?
     *
     * @param requestDTO 閫€鍗曡姹備俊鎭?
     * @return 閫€鍗曠粨鏋滀俊鎭?
     */
    Response<RefundMarketPayOrderResponseDTO> refundMarketPayOrder(RefundMarketPayOrderRequestDTO requestDTO);

}
