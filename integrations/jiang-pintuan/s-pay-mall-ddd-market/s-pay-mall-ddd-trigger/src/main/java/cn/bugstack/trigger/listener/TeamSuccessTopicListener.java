package cn.bugstack.trigger.listener;

import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @description 缁撶畻瀹屾垚娑堟伅鐩戝惉
 */
@Slf4j
@Component
public class TeamSuccessTopicListener {

    @Resource
    private IOrderService orderService;

    /**
     * 鎸囧畾娑堣垂闃熷垪
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_team_success.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_team_success.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_team_success.routing_key}"
            )
    )
    public void listener(String message) {
        try {
            NotifyRequestDTO requestDTO = JSON.parseObject(message, NotifyRequestDTO.class);
            log.info("鎷煎洟鍥炶皟锛岀粍闃熷畬鎴愶紝缁撶畻寮€濮?{}", JSON.toJSONString(requestDTO));
            // 钀ラ攢缁撶畻
            orderService.changeOrderMarketSettlement(requestDTO.getOutTradeNoList());
        } catch (Exception e) {
            log.error("鎷煎洟鍥炶皟锛岀粍闃熷畬鎴愶紝缁撶畻澶辫触 {}", message, e);
            throw e;
        }
    }

}
