package com.futao.springboot.learn.rabbitmq.rabbitmq.delay;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author futao
 * @date 2020/3/26.
 */
//@Component
public class Sender implements ApplicationRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send() {

        LocalDateTime now = LocalDateTime.now();
        CorrelationData correlationData = new CorrelationData(now.format(DateTimeFormatter.BASIC_ISO_DATE));

        rabbitTemplate.convertAndSend(Binding.DELAY_EXCHANGE, "delayed.routing.key.abc", now, new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                MessageProperties messageProperties = message.getMessageProperties();
                //设置消息的延迟时间
//                messageProperties.setHeader("x-delay", 3000);
                messageProperties.setDelay(5000);
                //消息过期---过期的消息会被发送到死信队列
                messageProperties.setExpiration("3000");
                return message;
            }
        }, correlationData);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        for (int i = 0; i < 10; i++) {
            send();
        }
    }
}
