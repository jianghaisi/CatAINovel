package cn.bugstack.domain.activity.service.discount.impl;

import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.service.discount.AbstractDiscountCalculateService;
import cn.bugstack.domain.activity.service.discount.IDiscountCalculateService;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description 婊″噺浼樻儬璁＄畻
 */
@Slf4j
@Service("MJ")
public class MJCalculateService extends AbstractDiscountCalculateService {

    @Override
    public BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        log.info("浼樻儬绛栫暐鎶樻墸璁＄畻:{}", groupBuyDiscount.getDiscountType().getCode());

        // 鎶樻墸琛ㄨ揪寮?- 100,10 婊?00鍑?0鍏?
        String marketExpr = groupBuyDiscount.getMarketExpr();
        String[] split = marketExpr.split(Constants.SPLIT);
        BigDecimal x = new BigDecimal(split[0].trim());
        BigDecimal y = new BigDecimal(split[1].trim());

        // 涓嶆弧瓒虫渶浣庢弧鍑忕害鏉燂紝鍒欐寜鐓у師浠?
        if (originalPrice.compareTo(x) < 0) {
            return originalPrice;
        }

        // 鎶樻墸浠锋牸
        BigDecimal deductionPrice = originalPrice.subtract(y);

        // 鍒ゆ柇鎶樻墸鍚庨噾棰濓紝鏈€浣庢敮浠?鍒嗛挶
        if (deductionPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("0.01");
        }

        return deductionPrice;
    }

}
