package cn.bugstack.trigger.listener;

import cn.bugstack.domain.trade.model.valobj.TeamRefundSuccess;
import cn.bugstack.domain.trade.service.ITradeRefundOrderService;
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
public class RefundSuccessTopicListener {

    @Resource
    private ITradeRefundOrderService tradeRefundOrderService;

    /**
     * 姝ゆ祦绋嬪叿澶囨渶缁堜竴鑷存€э紱
     * 1. 鏁版嵁搴撻攣鍗曢噺鎭㈠瀹屾垚锛屾湰鍦版秷鎭〃琛ュ伩MQ锛岀‘淇滿Q娑堟伅涓€瀹氫細鍙戦€併€?
     * 2. MQ 娑堟伅娑堣垂锛屾仮澶嶉攣鍗曢噺搴撳瓨銆傚簱瀛樻椂娣诲姞鍒嗗竷寮忛攣锛岀‘淇濅笉浼氶噸澶嶆搷浣溿€?
     * 3. MQ 娑堟伅閲嶈瘯锛岀‘淇濆湪澶辫触鎯呭喌涓嬶紝鍙互閲嶅娑堟伅锛屽張鍥犱负鏈夊垎甯冨紡閿佺殑澶勭悊锛屽彲浠ョ‘淇濋噸澶嶆秷璐逛篃涓嶄細閲嶅娣诲姞閿佸崟閲忓簱绮椼€?
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.producer.topic_team_refund.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.producer.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.producer.topic_team_refund.routing_key}"
            )
    )
    public void listener(String message) {
        log.info("鎺ユ敹娑堟伅锛堥€€鍗曟垚鍔燂級- 鎭㈠鎷煎洟闃熶紞閿佸崟閲?{}", message);
        TeamRefundSuccess teamRefundSuccess = JSON.parseObject(message, TeamRefundSuccess.class);
        try {
            tradeRefundOrderService.restoreTeamLockStock(teamRefundSuccess);
        } catch (Exception e) {
            log.info("鎺ユ敹娑堟伅锛堥€€鍗曟垚鍔燂級- 鎭㈠鎷煎洟闃熶紞閿佸崟閲忓け璐?{}", message, e);
            // 鎶涘紓甯革紝mq娑堟伅浼氶噸璇?
            throw new RuntimeException(e);
        }
    }

}
