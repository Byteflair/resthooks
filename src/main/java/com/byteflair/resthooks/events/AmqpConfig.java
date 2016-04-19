package com.byteflair.resthooks.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 19/04/16.
 */
@Configuration
@Slf4j
public class AmqpConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private Integer port;
    @Value("${spring.rabbitmq.username:}")
    private String username;
    @Value("${spring.rabbitmq.password:}")
    private String password;
    @Value("${resthooks.queue:resthooks.queue}")
    private String queueName;
    @Value("${resthooks.exchange:resthooks.exchange}")
    private String exchangeName;

    @Bean
    RabbitAdmin amqpAdmin() {
        return new RabbitAdmin(getAmqpConnectionFactory());
    }

    @Bean
    ConnectionFactory getAmqpConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        LOGGER.info("Creating AMQP connection factory on host {}:{}", host, port);
        if (!username.isEmpty()) {
            connectionFactory.setUsername(username);
            LOGGER.info("Logging to AMQP with username ", username);
        } else {
            LOGGER.warn("Logging to AMQP with NO username");
        }
        if (!password.isEmpty()) {
            connectionFactory.setPassword(password);
            LOGGER.info("Logging to AMQP with password {}", password.replaceAll(".", "*"));
        } else {
            LOGGER.warn("Logging to AMQP with NO password");
        }
        return connectionFactory;
    }

    @Bean
    AmqpTemplate amqpTemplate() {
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(500);
        policy.setMaxInterval(10000);
        policy.setMultiplier(10);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(policy);

        RabbitTemplate template = new RabbitTemplate(getAmqpConnectionFactory());
        template.setRetryTemplate(retryTemplate);

        return template;
    }

    @Bean
    Queue queue() {
        return new Queue(queueName, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(queueName);
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(EventReceiver eventReceiver) {
        return new MessageListenerAdapter(eventReceiver);
    }
}
