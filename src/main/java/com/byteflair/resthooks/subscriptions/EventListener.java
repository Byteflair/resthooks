package com.byteflair.resthooks.subscriptions;

import com.byteflair.resthooks.events.Event;
import com.byteflair.resthooks.events.EventService;
import com.byteflair.resthooks.events.EventStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 19/04/16.
 */
@Slf4j
public class EventListener implements MessageListener {
    private EventService eventService;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public EventListener(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void onMessage(Message message) {
        Event event = null;
        String payload = null;
        try {
            payload = new String(message.getBody());
            //parse payload just to check that it is a well formed JSON
            mapper.readValue(payload, Map.class);
            //if no exception thrown during parsing, save the event as delivery PENDING
            event = eventService.save(new Event(EventStatus.PENDING, payload, LocalDateTime.now(ZoneId.of("UTC"))));
        } catch (IOException e) {
            LOGGER.error("Payload is not valid JSON: {}", payload, e);
            //in case of a parsing error save the event as INVALID
            eventService.save(new Event(EventStatus.INVALID, payload, LocalDateTime.now(ZoneId.of("UTC"))));
        }
        if (null != event) {
            //for each subscription
            //eventService.trigger(subscription, event);
        }
    }
}
