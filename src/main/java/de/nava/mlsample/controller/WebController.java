package de.nava.mlsample.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

@Controller
public class WebController {

    private static final Logger logger = LoggerFactory.getLogger(WebController.class);

    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        model.put("now", new Date());
        return "index";
    }

    @RequestMapping("/viztest")
    public String viztest(Map<String, Object> model) {
        return "viztest";
    }

}
