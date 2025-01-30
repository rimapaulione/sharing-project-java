package com.sharingAPI.sharing.verification;

import com.sharingAPI.sharing.user.User;
import com.sharingAPI.sharing.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;


    public VerificationToken createVerificationToken(String email) {
        verificationTokenRepository.findByEmail(email)
                .ifPresent(verificationTokenRepository::delete);

        VerificationToken token = new VerificationToken();
        token.setToken(UUID.randomUUID().toString());
        token.setEmail(email);
        token.setExpires(LocalDateTime.now().plusMinutes(10));

        return verificationTokenRepository.save(token);
    }


    public VerificationTokenResponse verifyVerificationToken(String tokenValue) {
        Optional<VerificationToken> tokenOpt = verificationTokenRepository
                .findByToken(tokenValue);
        if (tokenOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }
        VerificationToken token = tokenOpt.get();

        if (token.getExpires().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(token);
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        Optional<User> userOpt = userRepository.findByEmail(token.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setVerified(LocalDateTime.now());
            userRepository.save(user);
        }
        var user = userRepository.findByEmail(token.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        verificationTokenRepository.delete(token);

        return VerificationTokenResponse.builder()
                .verified(user.getVerified())
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }


}
