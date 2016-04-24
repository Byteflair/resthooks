package com.byteflair.resthooks.subscriptions;

import com.byteflair.resthooks.events.Event;
import com.byteflair.resthooks.events.EventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 22/04/16.
 */
@Slf4j
public class EventPreprocessor implements MessageListener {
    private EventService eventService;
    private ObjectMapper mapper = new ObjectMapper();

    public EventPreprocessor(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public void onMessage(Message message) {
        String payload = null;
        Event event = null;
        try {
            payload = new String(message.getBody());
            //parse payload just to check that it is a well formed JSON
            mapper.readValue(payload, Map.class);
            //if no exception thrown during parsing, save the event as delivery PENDING
            event = eventService.save(new Event(payload, LocalDateTime.now(ZoneId.of("UTC")), true));
        } catch (IOException e) {
            LOGGER.error("Payload is not valid JSON: {}", payload, e);
            //in case of a parsing error save the event as INVALID
            event = eventService.save(new Event(payload, LocalDateTime.now(ZoneId.of("UTC")), false));
        }
        message.getMessageProperties().getHeaders().put("id", event.getId());
    }
}
