package com.byteflair.resthooks.events;

import com.byteflair.resthooks.journal.LogEntry;
import com.byteflair.resthooks.journal.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@Component
public class EventResourceProcessor implements ResourceProcessor<Resource<Event>> {
    private LogEntryRepository logEntryRepository;
    private RepositoryEntityLinks repositoryEntityLinks;

    @Autowired
    public EventResourceProcessor(LogEntryRepository logEntryRepository, RepositoryEntityLinks repositoryEntityLinks) {
        this.logEntryRepository = logEntryRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    @Override
    public Resource<Event> process(Resource<Event> eventResource) {
        List<LogEntry> history=logEntryRepository.findByEventIdOrderByTimestampDesc(eventResource.getContent().getId());
        eventResource.add(repositoryEntityLinks.linkToSearchResource(LogEntry.class, "journal").expand(eventResource.getContent().getId()));
        return eventResource;
    }
}
