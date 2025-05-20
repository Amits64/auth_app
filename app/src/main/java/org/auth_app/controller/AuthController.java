package org.auth_app.controller;

import org.auth_app.model.User;
import org.auth_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder) {
        this.userService     = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ——— Registration ———

    // Show the “Create Account” form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    // Handle Create Account submissions
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userForm") User user) {
        userService.save(user);
        // Redirect back to login, with a “justRegistered” flag
        return "redirect:/auth/login?registered";
    }

    // ——— Login ———

    // Show the login page (also at “/”)
    @GetMapping({ "/login", "/" })
    public String showLoginPage(
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {
        if (registered != null) {
            model.addAttribute("justRegistered", true);
        }
        return "login";
    }

    // (Spring Security itself handles the POST to /auth/login, so no @PostMapping here)

    // ——— Forgot Password ———

    // Show the "Forgot Password" form
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm(Model model) {
        return "forgot-password";
    }

    // Handle the "Forgot Password" form submission
    @PostMapping("/forgot-password")
    public String handleForgotPassword(
            @RequestParam("identifier") String identifier,
            Model model) {

        // TODO: lookup user by username/email,
        //       generate reset token + send email

        model.addAttribute("message",
            "If an account with that username/email exists, you’ll receive reset instructions shortly.");
        return "forgot-password";
    }
}
