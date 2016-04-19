package com.byteflair.resthooks.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@RepositoryRestResource(path = "events", collectionResourceRel = "events", itemResourceRel = "event")
interface EventRepository extends PagingAndSortingRepository<Event, String> {

    @Override
    @RestResource(exported = true)
    Event findOne(String s);

    @Override
    @RestResource(exported = true)
    Iterable<Event> findAll();

    @Override
    @RestResource(exported = true)
    Iterable<Event> findAll(Sort sort);

    @Override
    @RestResource(exported = true)
    Page<Event> findAll(Pageable pageable);

    @RestResource(path = "younger-than", rel = "younger-than")
    Page<Event> findByTimestampGreaterThan(@Param("timestamp") LocalDateTime timestamp, Pageable pageable);
}
