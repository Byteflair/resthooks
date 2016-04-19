package com.byteflair.resthooks.journal;

import com.byteflair.resthooks.events.Event;
import com.byteflair.resthooks.events.EventRepository;
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
    private EventRepository eventRepository;
    private RepositoryEntityLinks repositoryEntityLinks;

    @Autowired
    public LogEntryResourceProcessor(EventRepository eventRepository, RepositoryEntityLinks repositoryEntityLinks) {
        this.eventRepository = eventRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    @Override
    public Resource<LogEntry> process(Resource<LogEntry> logEntryResource) {
        Event event= eventRepository.findOne(logEntryResource.getContent().getEventId());
        logEntryResource.add(repositoryEntityLinks.linkToSingleResource(event));
        return logEntryResource;
    }
}
