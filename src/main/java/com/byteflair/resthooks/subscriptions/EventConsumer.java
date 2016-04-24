package com.byteflair.resthooks.subscriptions;

import com.byteflair.resthooks.events.Event;
import com.byteflair.resthooks.events.EventService;
import com.byteflair.resthooks.journal.EventLog;
import com.byteflair.resthooks.journal.EventLogRepository;
import com.byteflair.resthooks.journal.EventStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 19/04/16.
 */
@Slf4j
public class EventConsumer implements MessageListener {
    private EventService eventService;
    private EventLogRepository eventLogRepository;
    private SubscriptionRepository subscriptionRepository;
    private String subscriptionId;
    private RestTemplate restTemplate = new RestTemplate();
    private RetryTemplate template = new RetryTemplate();

    public EventConsumer(EventService eventService, EventLogRepository eventLogRepository, SubscriptionRepository subscriptionRepository, String subscriptionId) {
        this.eventService = eventService;
        this.eventLogRepository = eventLogRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionId = subscriptionId;
    }

    /**
     * For testatbility purposes
     *
     * @param restTemplate
     */
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void onMessage(Message message) {
        Event event = eventService.findOne((String) message.getMessageProperties().getHeaders().get("id"));
        if (event != null) {
            EventLog eventLog = null;
            if (event.isValid()) {
                eventLog = new EventLog(subscriptionId, event.getId(), EventStatus.PENDING,
                                        LocalDateTime.now(ZoneId.of("UTC")));
            } else {
                eventLog = new EventLog(subscriptionId, event.getId(), EventStatus.INVALID,
                                        LocalDateTime.now(ZoneId.of("UTC")));
            }
            eventLogRepository.save(eventLog);

            if (event.isValid()) {
                Subscription subscription = subscriptionRepository.findOne(subscriptionId);
                if (subscription != null) {
                    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
                    backOffPolicy.setInitialInterval(subscription.getInitialInterval());
                    backOffPolicy.setMaxInterval(subscription.getMaximunInterval());
                    backOffPolicy.setMultiplier(subscription.getMultiplier());
                    template.setBackOffPolicy(backOffPolicy);
                    template.execute(context->{
                        Link payload = new Link("", "");
                        ResponseEntity response = restTemplate
                            .postForEntity(subscription.getCallbackUrl(), payload, String.class);
                        EventLog requestLog = new EventLog(subscriptionId, event.getId(), EventStatus.ACKNOWLEDGED,
                                                           LocalDateTime.now(ZoneId.of("UTC")));
                        eventLogRepository.save(requestLog);
                        return response;
                    });
                }
            }
        }
    }

}
