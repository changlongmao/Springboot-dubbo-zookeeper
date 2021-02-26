package com.example.dubbo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: RabbitConfig
 * @Description:
 * @Author: Chang
 * @Date: 2021/02/23 14:23
 **/
@Slf4j
@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;
    // 定义一个或多个交换机
    public static final String EXCHANGE_A = "my-mq-exchange_A";
    public static final String EXCHANGE_B = "my-mq-exchange_B";

    // 定义队列
    public static final String QUEUE_A = "QUEUE_A";
    public static final String QUEUE_B = "QUEUE_B";

    // 定义routing-key
    public static final String ROUTING_KEY_A = "spring-boot-routingKey_A";
    public static final String ROUTING_KEY_B = "spring-boot-routingKey_B";


    /**
     * 配置消息交换机
     * 针对消费者配置
     * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念
     * HeadersExchange ：通过添加属性key-value匹配
     * DirectExchange:按照routingkey分发到指定队列
     * TopicExchange:多关键字匹配
     */
    @Bean
    public DirectExchange exchangeA() {
        // durable交换机持久化
        return new DirectExchange(EXCHANGE_A, true, false);
    }

    @Bean
    public DirectExchange exchangeB() {
        // durable交换机持久化
        return new DirectExchange(EXCHANGE_B, true, false);
    }

    /**
     * 配置链接信息
     *
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);

        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setPublisherConfirms(true); // 必须要设置
        return connectionFactory;
    }

    /**
     * 配置消息队列A
     * 针对消费者配置
     *
     * @return
     */
    @Bean
    public Queue queueA() {
        return new Queue(QUEUE_A, true); //队列持久
    }

    /**
     * 将消息队列A与交换机A绑定
     * 针对消费者配置
     *
     * @return
     */
    @Bean
    public Binding bindingA() {
        return BindingBuilder.bind(queueA()).to(exchangeA()).with(ROUTING_KEY_A);
    }

    /**
     * 配置消息队列B
     * 针对消费者配置
     *
     * @return
     */
    @Bean
    public Queue queueB() {
        return new Queue(QUEUE_B, true); //队列持久
    }

    /**
     * 将消息队列B与交换机B绑定
     * 针对消费者配置
     *
     * @return
     */
    @Bean
    public Binding bindingB() {
        return BindingBuilder.bind(queueB()).to(exchangeB()).with(ROUTING_KEY_B);
    }

    /**
     * 接受消息的监听，这个监听会接受消息队列A的消息
     * 针对消费者配置
     *
     * @return
     */
//    @Bean
    public SimpleMessageListenerContainer messageContainerA() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(queueA());
        container.setExposeListenerChannel(true);
        container.setMaxConcurrentConsumers(1);
        container.setConcurrentConsumers(1);
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL); //设置确认模式手工确认
        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
            byte[] body = message.getBody();
            log.info("监听器A收到消息 : " + new String(body));
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); //确认消息成功消费

        });
        return container;
    }

    /**
     * @Author Chang
     * @Description 创建rabbitTemplate
     * @Date 2021/2/23 14:44
     * @Return org.springframework.amqp.rabbit.core.RabbitTemplate
     **/
    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
//        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }
}
