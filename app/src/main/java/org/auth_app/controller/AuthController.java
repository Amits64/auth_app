// src/main/java/org/auth_app/controller/AuthController.java
package org.auth_app.controller;

import org.auth_app.model.User;
import org.auth_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ——— Registration ———

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userForm") User user) {
        userService.save(user);
        return "redirect:/auth/login?registered";
    }

    // ——— Login ———

    // Only map "/auth/login" (remove "/" here)
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {
        if (registered != null) {
            model.addAttribute("justRegistered", true);
        }
        return "login";
    }

    // (Spring Security handles POST to /auth/login)

    // ——— Forgot Password ———

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("identifier") String identifier,
            Model model) {
        // TODO: lookup user by username/email, generate reset token + send email
        model.addAttribute("message",
            "If an account with that username/email exists, you’ll receive reset instructions shortly.");
        return "forgot-password";
    }
}
