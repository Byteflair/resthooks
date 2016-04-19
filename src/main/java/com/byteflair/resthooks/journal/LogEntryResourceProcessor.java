package com.byteflair.resthooks.journal;

import com.byteflair.resthooks.events.Event;
import com.byteflair.resthooks.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@Component
public class LogEntryResourceProcessor implements ResourceProcessor<Resource<LogEntry>> {
    private EventService eventService;
    private RepositoryEntityLinks repositoryEntityLinks;

    @Autowired
    public LogEntryResourceProcessor(EventService eventService, RepositoryEntityLinks repositoryEntityLinks) {
        this.eventService = eventService;
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    @Override
    public Resource<LogEntry> process(Resource<LogEntry> logEntryResource) {
        Event event = eventService.findOne(logEntryResource.getContent().getEventId());
        logEntryResource.add(repositoryEntityLinks.linkToSingleResource(event));
        return logEntryResource;
    }
}
