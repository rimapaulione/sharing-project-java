package com.sharingAPI.sharing.auth;

import com.sharingAPI.sharing.user.Role;
import com.sharingAPI.sharing.user.User;
import com.sharingAPI.sharing.user.UserRepository;
import com.sharingAPI.sharing.verification.VerificationToken;
import com.sharingAPI.sharing.verification.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;



    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use: " + request.getEmail());
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .city(request.getCity())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .verified(null)
                .build();

        var savedUser =userRepository.save(user);

        VerificationToken verificationToken = verificationTokenService
                .createVerificationToken(savedUser.getEmail());

        return RegisterResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .city(user.getCity())
                .email(user.getEmail())
                .token(null)
                .role(user.getRole())
                .verification(verificationToken.getToken())
                .build();
    }


}
