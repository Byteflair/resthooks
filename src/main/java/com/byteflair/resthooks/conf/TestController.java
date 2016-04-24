package com.byteflair.resthooks.conf;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Daniel Cerecedo <daniel.cerecedo@byteflair.com> on 24/04/16.
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> test(String body) {
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
