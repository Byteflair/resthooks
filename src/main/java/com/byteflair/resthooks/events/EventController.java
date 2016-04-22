package com.byteflair.resthooks.events;

import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 22/04/16.
 */
@RepositoryRestController
public class EventController {
    @RequestMapping(method = RequestMethod.POST, value = "/events")
    public
    @ResponseBody
    ResponseEntity<Void> create() {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
