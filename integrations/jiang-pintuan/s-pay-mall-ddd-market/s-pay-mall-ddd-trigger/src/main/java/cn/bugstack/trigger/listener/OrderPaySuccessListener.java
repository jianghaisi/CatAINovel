package cn.bugstack.trigger.listener;

import cn.bugstack.domain.goods.service.IGoodsService;
import cn.bugstack.domain.order.adapter.event.PaySuccessMessageEvent;
import com.alibaba.fastjson.JSON;
import com.google.common.eventbus.Subscribe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description 鏀粯缁撶畻鎴愬姛鍥炶皟娑堟伅
 */
@Slf4j
@Component
public class OrderPaySuccessListener {

    @Resource
    private IGoodsService goodsService;

    // @Subscribe - 鏃х増鍙戝竷璁㈤槄鏂瑰紡
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_order_pay_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_order_pay_success.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_order_pay_success.routing_key}"
            )
    )
    public void listener(String paySuccessMessageJson) {
        try {
            log.info("鏀跺埌鏀粯鎴愬姛娑堟伅 {}", paySuccessMessageJson);

            PaySuccessMessageEvent.PaySuccessMessage paySuccessMessage = JSON.parseObject(paySuccessMessageJson, PaySuccessMessageEvent.PaySuccessMessage.class);

            log.info("妯℃嫙鍙戣揣锛堝锛涘彂璐с€佸厖鍊笺€佸紑鎴峰憳銆佽繑鍒╋級锛屽崟鍙?{}", paySuccessMessage.getTradeNo());

            // 鍙樻洿璁㈠崟鐘舵€?- 鍙戣揣瀹屾垚&缁撶畻
            goodsService.changeOrderDealDone(paySuccessMessage.getTradeNo());

            // 鍙互鎵撳紑娴嬭瘯锛孧Q 娑堣垂澶辫触锛屼細鎶涘紓甯革紝涔嬪悗閲嶈瘯娑堣垂銆傝繖涓篃鏄渶缁堟墽琛岀殑閲嶈鎵嬫銆?
            // throw new RuntimeException("閲嶈瘯娑堣垂");
        } catch (Exception e) {
            log.error("鏀跺埌鏀粯鎴愬姛娑堟伅澶辫触 {}", paySuccessMessageJson,e);
            throw e;
        }
    }

}
