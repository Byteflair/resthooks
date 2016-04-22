package com.byteflair.resthooks.conf;

import com.byteflair.resthooks.serialization.LocalDateTimeDeserializer;
import com.byteflair.resthooks.serialization.LocalDateTimeSerializer;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import java.time.LocalDateTime;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
public class ResthooksModule extends SimpleModule {
    public ResthooksModule() {
        super("ResthooksModule", new Version(1, 0, 0, "SNAPSHOT", "com.byteflair", "byteflair-resthooks"));
    }
    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        SimpleSerializers serializers= new SimpleSerializers();
        serializers.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        context.addSerializers(serializers);

        SimpleDeserializers deserializers = new SimpleDeserializers();
        deserializers.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        context.addDeserializers(deserializers);
    }

}
