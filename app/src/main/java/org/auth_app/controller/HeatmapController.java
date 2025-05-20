package org.auth_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HeatmapController {

    @GetMapping({ "/heatmap", "/heatmap.html" })
    public String heatmap() {
        return "heatmap";  // resolves to templates/heatmap.html
    }
}
