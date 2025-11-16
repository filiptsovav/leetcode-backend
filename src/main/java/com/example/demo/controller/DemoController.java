package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    private static final Logger log = LoggerFactory.getLogger(DemoController.class);

    @GetMapping("/")
    public String redirectToIndex() {
        log.info("GET / — редирект на /login");
        return "forward:/login";
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        log.info("GET /dashboard");
        return "forward:/dashboard.html";
    }

    @GetMapping("/taskChosen")
    public String taskChosen() {
        log.info("GET /taskChosen");
        return "taskChosen";
    }
}
