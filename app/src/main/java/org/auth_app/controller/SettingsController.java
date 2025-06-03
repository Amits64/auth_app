// src/main/java/org/auth_app/controller/SettingsController.java
package org.auth_app.controller;

import org.auth_app.model.User;
import org.auth_app.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    // If you allow a POST to save settings:
    // @PostMapping("/settings")
    // public String saveSettings(@ModelAttribute("user") User formUser, Principal principal) {
    //     formUser.setUsername(principal.getName());
    //     userService.updateSettings(formUser);
    //     return "redirect:/settings?success";
    // }
}
