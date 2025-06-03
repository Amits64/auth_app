package org.auth_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportsController {

    @GetMapping("/reports")
    public String reports(Model model) {
        // Example: populate "recentReports" for Thymeleaf
        // List<ReportActivity> recentReports = reportService.findRecent();
        // model.addAttribute("recentReports", recentReports);
        return "reports";
    }
}
