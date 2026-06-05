package cn.bugstack.domain.activity.service.discount;

import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.math.BigDecimal;

/**
 * @description 鎶樻墸璁＄畻鏈嶅姟
 */
public interface IDiscountCalculateService {

    /**
     * 鎶樻墸璁＄畻
     *
     * @param userId           鐢ㄦ埛ID
     * @param originalPrice    鍟嗗搧鍘熷浠锋牸
     * @param groupBuyDiscount 鎶樻墸璁″垝閰嶇疆
     * @return 鍟嗗搧浼樻儬浠锋牸
     */
    BigDecimal calculate(String userId, BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount);

}
