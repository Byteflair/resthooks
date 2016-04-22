package com.byteflair.resthooks.subscriptions;

import com.byteflair.resthooks.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 22/04/16.
 */
@Configuration
public class SubscriptionConfig {
    @Bean
    @Autowired
    SubscriptionHandler subscriptionService(AmqpService amqpService, EventService eventService) {
        return new SubscriptionHandler(amqpService, eventService);
    }
}
