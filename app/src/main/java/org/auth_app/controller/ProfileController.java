// src/main/java/org/auth_app/controller/ProfileController.java
package org.auth_app.controller;

import org.auth_app.model.User;
import org.auth_app.service.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Principal principal) {
        // 1) Figure out the currently authenticated username:
        String username = principal.getName();

        // 2) Unwrap Optional<User> → User, or throw if not found:
        User user = userService.findByUsername(username)
                               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 3) Add it into the model under the name “user” (must match th:object="${user}" below)
        model.addAttribute("user", user);
        return "profile";
    }

    // If you want to allow POST → /profile to save updates:
    // @PostMapping("/profile")
    // public String updateProfile(@ModelAttribute("user") User formUser, Principal principal) {
    //     // re-attach the username field or ID to be safe
    //     formUser.setUsername(principal.getName());
    //     userService.updateUser(formUser);
    //     return "redirect:/profile?success";
    // }
}
