package cn.bugstack.test.trigger;

import cn.bugstack.api.IDCCService;
import cn.bugstack.domain.activity.model.entity.MarketProductEntity;
import cn.bugstack.domain.activity.model.entity.TrialBalanceEntity;
import cn.bugstack.domain.activity.service.IIndexGroupBuyMarketService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @description 鍔ㄦ€侀厤缃鐞嗘祴璇?
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class DCCControllerTest {

    @Resource
    private IDCCService dccService;

    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;

    @Test
    public void test_updateConfig() {
        // 鍔ㄦ€佽皟鏁撮厤缃?
        dccService.updateConfig("downgradeSwitch", "1");
    }

    @Test
    public void test_updateConfig2indexMarketTrial() throws Exception {
        // 鍔ㄦ€佽皟鏁撮厤缃?
        dccService.updateConfig("downgradeSwitch", "1");
        // 瓒呮椂绛夊緟寮傛
        Thread.sleep(1000);

        // 钀ラ攢楠岃瘉
        MarketProductEntity marketProductEntity = new MarketProductEntity();
        marketProductEntity.setUserId("jiang");
        marketProductEntity.setSource("s01");
        marketProductEntity.setChannel("c01");
        marketProductEntity.setGoodsId("9890001");

        TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(marketProductEntity);
        log.info("璇锋眰鍙傛暟:{}", JSON.toJSONString(marketProductEntity));
        log.info("杩斿洖缁撴灉:{}", JSON.toJSONString(trialBalanceEntity));
    }


}
