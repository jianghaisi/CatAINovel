package cn.bugstack.domain.activity.service.discount.impl;

import cn.bugstack.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import cn.bugstack.domain.activity.service.discount.AbstractDiscountCalculateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description 婊″噺浼樻儬璁＄畻
 */
@Slf4j
@Service("N")
public class NCalculateService extends AbstractDiscountCalculateService {

    @Override
    public BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        log.info("浼樻儬绛栫暐鎶樻墸璁＄畻:{}", groupBuyDiscount.getDiscountType().getCode());

        // 鎶樻墸琛ㄨ揪寮?- 鐩存帴涓轰紭鎯犲悗鐨勯噾棰?
        String marketExpr = groupBuyDiscount.getMarketExpr();
        // n鍏冭喘
        return new BigDecimal(marketExpr);
    }

}
