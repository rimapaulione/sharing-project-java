package com.sharingAPI.sharing.verification;

import com.sharingAPI.sharing.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/verification")
public class VerificationTokenController {

    private final VerificationTokenService verificationTokenService;
    private  final UserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createVerificationToken(
            @RequestParam String email) {

        VerificationToken token = verificationTokenService.createVerificationToken(email);
        Map<String, String> response = new HashMap<>();
        response.put("token", token.getToken());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify")
    public ResponseEntity<VerificationTokenResponse> verifyToken(
            @RequestParam String token) {
        return ResponseEntity.ok(verificationTokenService.verifyVerificationToken(token));
    }


}
