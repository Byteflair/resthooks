package com.byteflair.resthooks.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 19/04/16.
 */
@Configuration
public class EventConfig {
    @Bean
    @Autowired
    EventService eventService(EventRepository eventRepository) {
        return new EventService(eventRepository);
    }

    @Bean
    @Autowired
    EventReceiver eventReceiver(EventService eventService) {
        return new EventReceiver(eventService);
    }
}
