package com.byteflair.resthooks.journal;

import com.byteflair.resthooks.events.Event;
import com.byteflair.resthooks.events.EventService;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
public class EventLogResourceProcessor implements ResourceProcessor<Resource<EventLog>> {
    private EventService eventService;
    private RepositoryEntityLinks repositoryEntityLinks;

    public EventLogResourceProcessor(EventService eventService, RepositoryEntityLinks repositoryEntityLinks) {
        this.eventService = eventService;
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    @Override
    public Resource<EventLog> process(Resource<EventLog> logEntryResource) {
        Event event = eventService.findOne(logEntryResource.getContent().getEventId());
        logEntryResource.add(repositoryEntityLinks.linkToSingleResource(event));
        return logEntryResource;
    }
}
