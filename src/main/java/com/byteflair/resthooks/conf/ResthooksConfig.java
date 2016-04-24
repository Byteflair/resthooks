package com.byteflair.resthooks.conf;

import com.byteflair.resthooks.events.EventConfig;
import com.byteflair.resthooks.journal.JournalConfig;
import com.byteflair.resthooks.subscriptions.AmqpConfig;
import com.byteflair.resthooks.subscriptions.SubscriptionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@Configuration
@Import({AmqpConfig.class, SubscriptionConfig.class, EventConfig.class, JournalConfig.class})
public class ResthooksConfig extends RepositoryRestConfigurerAdapter{
    @Override
    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new ResthooksModule());
        super.configureJacksonObjectMapper(objectMapper);
    }
}
