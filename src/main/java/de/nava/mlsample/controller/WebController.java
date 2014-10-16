package de.nava.mlsample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.Map;

@Controller
public class WebController {

    @RequestMapping("/")
    public String welcome(Map<String, Object> model) {
        model.put("now", new Date());
        return "index";
    }

    @RequestMapping("/viztest")
    public String viztest(Map<String, Object> model) {
        return "viztest";
    }

}
