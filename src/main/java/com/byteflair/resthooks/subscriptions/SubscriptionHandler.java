package com.byteflair.resthooks.subscriptions;

import com.byteflair.resthooks.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 20/04/16.
 */
@RepositoryEventHandler(Subscription.class)
public class SubscriptionHandler {
    private AmqpService amqpService;
    private EventService eventService;

    @Autowired
    public SubscriptionHandler(AmqpService amqpService, EventService eventService) {
        this.amqpService = amqpService;
        this.eventService = eventService;
    }

    @HandleAfterCreate
    public void create(Subscription subscription) {
        amqpService.subscribe(subscription.getTopic(), new EventListener(eventService));
    }
}
