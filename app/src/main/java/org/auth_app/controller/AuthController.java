// src/main/java/org/auth_app/controller/AuthController.java
package org.auth_app.controller;

import org.auth_app.model.User;
import org.auth_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authManager;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authManager) {
        this.userService = userService;
        this.authManager = authManager;
    }

    // ——— Registration Form ———
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userForm", new User());
        return "registration";
    }

    // ——— Handle Registration POST ———
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("userForm") User user) {
        String rawPassword = user.getPassword();
        userService.save(user);  // encodes & saves
        Authentication authReq =
            new UsernamePasswordAuthenticationToken(user.getUsername(), rawPassword);
        Authentication auth = authManager.authenticate(authReq);
        SecurityContextHolder.getContext().setAuthentication(auth);
        // Redirect to MFA setup (handled by MfaController)
        return "redirect:/mfa/setup";
    }

    // ——— Login Form ———
    @GetMapping("/login")
    public String showLoginPage(
        @RequestParam(value = "registered", required = false) String reg,
        Model model
    ) {
        if (reg != null) model.addAttribute("justRegistered", true);
        return "login";
    }

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
    // ——— Handle Login POST ———
    @PostMapping("/login")
    public String loginUser(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        Model model
    ) {
        try {
            Authentication authReq =
                new UsernamePasswordAuthenticationToken(username, password);
            Authentication auth = authManager.authenticate(authReq);
            SecurityContextHolder.getContext().setAuthentication(auth);
            return "redirect:/dashboard"; // or wherever you want to go after login
        } catch (BadCredentialsException e) {
            model.addAttribute("loginError", "Invalid username or password");
            return "login";
        }
    }
    // ——— Logout ———
    @GetMapping("/auth/logout")
    public String logout() {
        SecurityContextHolder.clearContext(); // clear session
        return "redirect:/auth/login?logout"; // redirect to login with logout message
    }
}