package com.byteflair.resthooks.events;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 19/04/16.
 */
public class EventService {
    private EventRepository eventRepository;

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
