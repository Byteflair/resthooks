package com.byteflair.resthooks.events;

import com.byteflair.resthooks.journal.EventLog;
import com.byteflair.resthooks.journal.EventLogRepository;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;

import java.util.List;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
public class EventResourceProcessor implements ResourceProcessor<Resource<Event>> {
    private EventLogRepository eventLogRepository;
    private RepositoryEntityLinks repositoryEntityLinks;

    public EventResourceProcessor(EventLogRepository eventLogRepository, RepositoryEntityLinks repositoryEntityLinks) {
        this.eventLogRepository = eventLogRepository;
        this.repositoryEntityLinks = repositoryEntityLinks;
    }

    @Override
    public Resource<Event> process(Resource<Event> eventResource) {
        List<EventLog> history = eventLogRepository
            .findByEventIdOrderByTimestampDesc(eventResource.getContent().getId());
        eventResource.add(repositoryEntityLinks.linkToSearchResource(EventLog.class, "journal")
                                               .expand(eventResource.getContent().getId()));
        return eventResource;
    }
}
