package cn.bugstack.trigger.listener;

import cn.bugstack.api.dto.NotifyRequestDTO;
import cn.bugstack.api.dto.TeamRefundSuccessRequestDTO;
import cn.bugstack.domain.order.service.IOrderService;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 钀ラ攢閫€鍗曟垚鍔熸秷鎭?
 *
 * 2025/8/1 09:52
 */
@Slf4j
@Component
public class RefundSuccessTopicListener {

    @Resource
    private IOrderService orderService;

    /**
     * 鎸囧畾娑堣垂闃熷垪
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.consumer.topic_team_refund.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.consumer.topic_team_refund.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.consumer.topic_team_refund.routing_key}"
            )
    )
    public void listener(String message) {
        try {
            log.info("閫€鍗曞洖璋冿紝鍙戣捣閫€娆?{}", message);
            TeamRefundSuccessRequestDTO requestDTO = JSON.parseObject(message, TeamRefundSuccessRequestDTO.class);
            String type = requestDTO.getType();
            if ("paid_unformed".equals(type) || "paid_formed".equals(type)) {
                orderService.refundPayOrder(requestDTO.getUserId(), requestDTO.getOutTradeNo());
            }
        } catch (AlipayApiException ex) {
            throw new RuntimeException(ex);
        } catch (Exception e) {
            log.error("鎷煎洟鍥炶皟锛岄€€鍗曞畬鎴愶紝閫€娆惧け璐?{}", message, e);
            throw e;
        }
    }

}
