package org.auth_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NewsController {

    @GetMapping({ "/news", "/news.html" })
    public String news() {
        return "news";  // resolves to templates/news.html
    }
}
