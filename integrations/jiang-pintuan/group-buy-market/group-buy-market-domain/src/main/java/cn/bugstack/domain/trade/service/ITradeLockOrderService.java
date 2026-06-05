package cn.bugstack.domain.trade.service;

import cn.bugstack.domain.trade.model.entity.*;
import cn.bugstack.domain.trade.model.valobj.GroupBuyProgressVO;

/**
 * @description 鎷煎洟浜ゆ槗閿佸崟鏈嶅姟鎺ュ彛
 */
public interface ITradeLockOrderService {

    /**
     * 鏌ヨ锛屾湭琚敮浠樻秷璐瑰畬鎴愮殑钀ラ攢浼樻儬璁㈠崟
     *
     * @param userId     鐢ㄦ埛ID
     * @param outTradeNo 澶栭儴鍞竴鍗曞彿
     * @return 鎷煎洟锛岄璐鍗曡惀閿€瀹炰綋瀵硅薄
     */
    MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outTradeNo);

    /**
     * 鏌ヨ鎷煎洟杩涘害
     *
     * @param teamId 鎷煎洟ID
     * @return 杩涘害
     */
    GroupBuyProgressVO queryGroupBuyProgress(String teamId);

    /**
     * 閿佸畾锛岃惀閿€棰勬敮浠樿鍗曪紱鍟嗗搧涓嬪崟鍓嶏紝棰勮喘閿佸畾銆?
     *
     * @param userEntity        鐢ㄦ埛鏍瑰疄浣撳璞?
     * @param payActivityEntity 鎷煎洟锛屾敮浠樻椿鍔ㄥ疄浣撳璞?
     * @param payDiscountEntity 鎷煎洟锛屾敮浠樹紭鎯犲疄浣撳璞?
     * @return 鎷煎洟锛岄璐鍗曡惀閿€瀹炰綋瀵硅薄
     */
    MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) throws Exception;

}
