package com.byteflair.resthooks.subscriptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Value("${resthooks.exchange:firehose}")
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
    @Autowired
    AmqpService amqpService(AmqpAdmin amqpAdmin, ConnectionFactory connectionFactory, AmqpTemplate amqpTemplate) {
        return new AmqpService(amqpAdmin, connectionFactory, amqpTemplate);
    }

}
