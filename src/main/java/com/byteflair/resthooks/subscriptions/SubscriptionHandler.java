package com.byteflair.resthooks.subscriptions;

import com.byteflair.resthooks.events.EventService;
import com.byteflair.resthooks.journal.EventLogRepository;
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
    private EventLogRepository eventLogRepository;
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    public SubscriptionHandler(AmqpService amqpService, EventService eventService, EventLogRepository eventLogRepository, SubscriptionRepository subscriptionRepository) {
        this.amqpService = amqpService;
        this.eventService = eventService;
        this.eventLogRepository = eventLogRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.amqpService.setEventPreProcessor(new EventPreprocessor(eventService));
    }

    @HandleAfterCreate
    public void create(Subscription subscription) {
        amqpService.subscribe(subscription.getTopic(),
                              new EventConsumer(eventService, eventLogRepository, subscriptionRepository,
                                                subscription.getId()));
    }
}
