package cn.bugstack.trigger.job;

import cn.bugstack.domain.order.service.IOrderService;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @description 妫€娴嬫湭鎺ユ敹鍒版垨鏈纭鐞嗙殑鏀粯鍥炶皟閫氱煡
 */
@Slf4j
@Component()
public class NoPayNotifyOrderJob {

    @Resource
    private IOrderService orderService;
    @Resource
    private AlipayClient alipayClient;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void exec() {
        try {
            log.info("浠诲姟锛涙娴嬫湭鎺ユ敹鍒版垨鏈纭鐞嗙殑鏀粯鍥炶皟閫氱煡");
            List<String> orderIds = orderService.queryNoPayNotifyOrder();
            if (null == orderIds || orderIds.isEmpty()) return;

            for (String orderId : orderIds) {
                AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
                AlipayTradeQueryModel bizModel = new AlipayTradeQueryModel();
                bizModel.setOutTradeNo(orderId);
                request.setBizModel(bizModel);

                AlipayTradeQueryResponse alipayTradeQueryResponse = alipayClient.execute(request);
                String code = alipayTradeQueryResponse.getCode();

                // 鍒ゆ柇鐘舵€佺爜
                if ("10000".equals(code)) {
                    orderService.changeOrderPaySuccess(orderId, alipayTradeQueryResponse.getSendPayDate());
                }
            }
        } catch (Exception e) {
            log.error("妫€娴嬫湭鎺ユ敹鍒版垨鏈纭鐞嗙殑鏀粯鍥炶皟閫氱煡澶辫触", e);
        }
    }

}
