package cn.bugstack.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class RabbitMQConfig {

    /**
     * 娑堣垂鎷煎洟娑堟伅
     */
    @Bean
    public Binding topicTeamSuccessBinding(
            @Value("${spring.rabbitmq.config.consumer.topic_team_success.exchange}") String exchangeName,
            @Value("${spring.rabbitmq.config.consumer.topic_team_success.routing_key}") String routingKey,
            @Value("${spring.rabbitmq.config.consumer.topic_team_success.queue}") String queue) {

        // 娑堟伅鐢熶骇鏂圭殑浜ゆ崲鏈?
        TopicExchange topicExchange = new TopicExchange(exchangeName, true, false);

        return BindingBuilder.bind(new Queue(queue, true))
                .to(topicExchange)
                .with(routingKey);
    }

}
