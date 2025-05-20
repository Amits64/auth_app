package org.auth_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FundamentalController {

    @GetMapping({ "/fundamentals", "/fundamentals.html" })
    public String fundamentals() {
        return "fundamentals";  // resolves to templates/fundamentals.html
    }
}
