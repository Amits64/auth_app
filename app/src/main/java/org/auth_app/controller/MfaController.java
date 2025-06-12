// src/main/java/org/auth_app/controller/MfaController.java
package org.auth_app.controller;

import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import jakarta.servlet.http.HttpSession;

import org.auth_app.model.User;
import org.auth_app.repository.UserRepository;
import org.auth_app.security.MfaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Controller
@RequestMapping("/mfa")
public class MfaController {

    private final MfaService mfaService;
    private final UserRepository userRepository;

    public MfaController(MfaService mfaService, UserRepository userRepository) {
        this.mfaService = mfaService;
        this.userRepository = userRepository;
    }

    @GetMapping("/setup")
    public String showSetup(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        GoogleAuthenticatorKey key;
        String secret;
        if (user.getMfaSecret() == null) {
            key = mfaService.createCredentials();
            secret = mfaService.getSecret(key);
            user.setMfaSecret(secret);
            userRepository.save(user);
        } else {
            secret = user.getMfaSecret();
            key = null; // not needed, but keeps variable for compatibility
        }

        // build the otpauth:// URI
        String otpAuth = mfaService.getOtpAuthUri(username, key);

        // now point directly at QRServer
        String qrUrl = "https://api.qrserver.com/v1/create-qr-code/"
                    + "?data=" + URLEncoder.encode(otpAuth, StandardCharsets.UTF_8)
                    + "&size=200x200";

        model.addAttribute("qrUri", qrUrl);
        model.addAttribute("secret", user.getMfaSecret());
        return "mfa-setup";
    }

    @PostMapping("/setup")
    public String confirmSetup(
            @RequestParam int code,
            Model model,
            Principal principal
    ) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (mfaService.verifyCode(user.getMfaSecret(), code)) {
            user.setMfaEnabled(true);
            userRepository.save(user);
            return "redirect:/settings?mfasetup";
        }

        model.addAttribute("error", "Invalid code, please try again.");
        // re-display QR + secret on failure
        return showSetup(model, principal);
    }

    /** After password login, show this if MFA is enabled or if there was a verify‐error */
    @GetMapping("/validate")
    public String showValidate(
            @RequestParam(value = "error", required = false) boolean error,
            Model model
    ) {
        model.addAttribute("error", error);
        return "mfa-validate";
    }

    /** Handle the submitted OTP */
    @PostMapping("/validate")
    public String validateCode(
        @RequestParam int code,
        Principal principal,
        HttpSession session
    ) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        if (mfaService.verifyCode(user.getMfaSecret(), code)) {
            session.setAttribute("MFA_AUTHENTICATED", true);
            return "redirect:/dashboard";
        }
        // on failure, redirect back to GET /mfa/validate?error
        return "redirect:/mfa/validate?error=true";
    }
}
