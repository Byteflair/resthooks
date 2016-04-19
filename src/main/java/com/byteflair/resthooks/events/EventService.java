package com.byteflair.resthooks.events;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 19/04/16.
 */
public class EventService {
    private EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Event findOne(String s) {
        return eventRepository.findOne(s);
    }

    public Event save(Event event) {
        return eventRepository.save(event);
    }
}
