package com.byteflair.resthooks.journal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.hateoas.Identifiable;

import java.time.LocalDateTime;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Document(collection = "journal")
public class EventLog implements Identifiable<String> {
    @Field
    @Id
    private String id;
    @Field
    private String subscriptionId;
    @Field
    private String eventId;
    @Field
    private EventStatus status;
    @Field
    private LocalDateTime timestamp;

    public EventLog(String subscriptionId, String eventId, EventStatus status, LocalDateTime timestamp) {
        this.subscriptionId = subscriptionId;
        this.eventId = eventId;
        this.status = status;
        this.timestamp = timestamp;
    }
}
