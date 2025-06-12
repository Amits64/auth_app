// src/main/java/org/auth_app/controller/SettingsController.java
package org.auth_app.controller;

import org.auth_app.model.User;
import org.auth_app.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class SettingsController {

    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        model.addAttribute("user", user);
        return "settings";
    }

    @PostMapping("/settings/save")
    public String savePreferences(
            @RequestParam(name = "enableNotifications", defaultValue = "false") boolean enableNotifications,
            @RequestParam(name = "darkMode",             defaultValue = "false") boolean darkMode,
            @RequestParam(name = "language")                   String  language,
            Principal principal
    ) {
        // You must implement this in your UserService:
        userService.updatePreferences(principal.getName(), enableNotifications, darkMode, language);
        return "redirect:/settings?preferencesSaved";
    }

    // (You still have your profile‐save code commented out below, if you want it.)
}
