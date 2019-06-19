package leyou.mq;

import com.aliyuncs.exceptions.ClientException;
import leyou.common.utils.JsonUtils;
import leyou.sms.config.SmsProperties;
import leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(SmsProperties.class)
public class SmsListener {
    @Autowired
    private SmsUtils smsUtils;

    @Autowired
    private SmsProperties prop;

    /**
     * 发送短信验证码
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "sms.verify.code.queue", durable = "true"),
            exchange = @Exchange(name = "ly.sms.exchange", type = ExchangeTypes.TOPIC),
            key = {"sms.verity.code"}
    ))
    public void listenInsertOrUpdate(Map<String, String> msg){
        if (CollectionUtils.isEmpty(msg)){
            return;
        }
        String phone = msg.remove("phone");
        if (StringUtils.isBlank(phone)){
            return;
        }
        //发送短信
        smsUtils.sendSms(phone,prop.getSignName(), prop.getVerifyCodeTemplate(), JsonUtils.toString(msg));

    }

}
