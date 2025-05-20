package org.auth_app.controller;

import org.auth_app.model.User;
import org.auth_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;           // ← change here
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller                                            // ← not @RestController
@RequestMapping("/auth")
public class RegistrationController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserService userService,
                                  PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ① Serve the registration form at GET /auth/register
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";     // Thymeleaf template registration.html
    }

    // ② Handle form POST from /auth/register
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userForm") User user) {
        userService.save(user);    // encoder already applied in UserService
        return "redirect:/login?registered";  // go back to login page
    }
}
