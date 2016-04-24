package com.byteflair.resthooks.journal;

import com.byteflair.resthooks.events.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 22/04/16.
 */
@Configuration
public class JournalConfig {
    @Bean
    @Autowired
    EventLogResourceProcessor eventLogResourceProcessor(EventService eventService, RepositoryEntityLinks repositoryEntityLinks) {
        return new EventLogResourceProcessor(eventService, repositoryEntityLinks);
    }
}
