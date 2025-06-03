// src/main/java/org/auth_app/controller/DashboardController.java
package org.auth_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    // Only map "/dashboard" here (remove "/" mapping)
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
