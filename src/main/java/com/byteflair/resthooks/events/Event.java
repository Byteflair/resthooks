package com.byteflair.resthooks.events;

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
@Document(collection = "events")
public class Event implements Identifiable<String>{
    @Field
    @Id
    private String id;
    @Field
    private String payload;
    @Field
    private LocalDateTime timestamp;
    @Field
    private boolean valid;

    public Event(String payload, LocalDateTime timestamp, boolean valid) {
        this.payload = payload;
        this.timestamp = timestamp;
        this.valid = valid;
    }
}
