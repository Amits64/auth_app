package org.auth_app.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GlobalErrorController implements ErrorController {

    @GetMapping("/error")
    public String error() {
        return "error"; // renders src/main/resources/templates/error.html
    }

    // Since Spring Boot 2.3+, ErrorController is a marker interface.
    // You don’t need to override getErrorPath() unless you’re on an older version.
}
