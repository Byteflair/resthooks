package com.byteflair.resthooks.journal;

import lombok.*;
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
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "journal")
public class LogEntry implements Identifiable<String>{
    @Field
    @Id
    private String id;
    @Field
    private String eventId;
    @Field
    private LogLevel level;
    @Field
    private String message;
    @Field
    private LocalDateTime timestamp;
}
