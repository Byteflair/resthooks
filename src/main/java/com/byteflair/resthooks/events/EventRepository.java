package com.byteflair.resthooks.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@RepositoryRestResource(path = "events", collectionResourceRel = "events", itemResourceRel = "event")
public interface EventRepository extends PagingAndSortingRepository<Event, String> {

    @RestResource(path = "younger-than", rel = "younger-than")
    Page<Event> findByTimestampGreaterThan(@Param("timestamp") LocalDateTime timestamp, Pageable pageable);
}
