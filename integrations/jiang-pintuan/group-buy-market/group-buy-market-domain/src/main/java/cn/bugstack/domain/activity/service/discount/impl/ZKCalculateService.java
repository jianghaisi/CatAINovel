package cn.bugstack.domain.activity.service.discount.impl;

import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.service.discount.AbstractDiscountCalculateService;
import cn.bugstack.domain.activity.service.discount.IDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description 鎶樻墸浼樻儬璁＄畻
 */
@Slf4j
@Service("ZK")
public class ZKCalculateService extends AbstractDiscountCalculateService {

    @Override
    public BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        log.info("浼樻儬绛栫暐鎶樻墸璁＄畻:{}", groupBuyDiscount.getDiscountType().getCode());

        // 鎶樻墸琛ㄨ揪寮?- 鎶樻墸鐧惧垎姣?
        String marketExpr = groupBuyDiscount.getMarketExpr();

        // 鎶樻墸浠锋牸 + 鍥涜垗浜斿叆
        BigDecimal deductionPrice = originalPrice.multiply(new BigDecimal(marketExpr)).setScale(0, RoundingMode.DOWN);

        // 鍒ゆ柇鎶樻墸鍚庨噾棰濓紝鏈€浣庢敮浠?鍒嗛挶
        if (deductionPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.01");
        }

        return deductionPrice;
    }

}
