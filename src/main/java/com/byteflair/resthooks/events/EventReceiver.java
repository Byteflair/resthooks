package com.byteflair.resthooks.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 19/04/16.
 */
@Slf4j
public class EventReceiver {
    private EventService eventService;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public EventReceiver(EventService eventService) {
        this.eventService = eventService;
    }

    public void handleMessage(byte[] payload) {
        handleMessage(new String(payload));
    }

    public void handleMessage(String payload) {
        try {
            mapper.readValue(payload, Map.class);
            eventService.save(new Event(EventStatus.PENDING, payload, LocalDateTime.now(ZoneId.of("UTC"))));
        } catch (IOException e) {
            LOGGER.error("Payload is not valid JSON: {}", payload, e);
            eventService.save(new Event(EventStatus.INVALID, payload, LocalDateTime.now(ZoneId.of("UTC"))));
        }
    }
}
