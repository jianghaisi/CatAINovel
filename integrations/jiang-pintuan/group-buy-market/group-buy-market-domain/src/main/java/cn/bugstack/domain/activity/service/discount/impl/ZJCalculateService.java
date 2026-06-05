package cn.bugstack.domain.activity.service.discount.impl;

import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.service.discount.AbstractDiscountCalculateService;
import cn.bugstack.domain.activity.service.discount.IDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description 鐩村噺
 */
@Slf4j
@Service("ZJ")
public class ZJCalculateService extends AbstractDiscountCalculateService {

    @Override
    public BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        log.info("浼樻儬绛栫暐鎶樻墸璁＄畻:{}", groupBuyDiscount.getDiscountType().getCode());

        // 鎶樻墸琛ㄨ揪寮?- 鐩村噺涓烘墸鍑忛噾棰?
        String marketExpr = groupBuyDiscount.getMarketExpr();

        // 鎶樻墸浠锋牸
        BigDecimal deductionPrice = originalPrice.subtract(new BigDecimal(marketExpr));

        // 鍒ゆ柇鎶樻墸鍚庨噾棰濓紝鏈€浣庢敮浠?鍒嗛挶
        if (deductionPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.01");
        }

        return deductionPrice;
    }

}
