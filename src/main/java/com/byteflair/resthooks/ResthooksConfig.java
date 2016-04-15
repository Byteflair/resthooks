package com.byteflair.resthooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.hateoas.ResourceProcessor;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 15/04/16.
 */
@Configuration
public class ResthooksConfig extends RepositoryRestConfigurerAdapter{
    @Override
    public void configureJacksonObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new ResthooksModule());
        super.configureJacksonObjectMapper(objectMapper);
    }
}
