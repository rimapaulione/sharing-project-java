package com.sharingAPI.sharing.auth;

import com.sharingAPI.sharing.config.service.JwtService;
import com.sharingAPI.sharing.token.Token;
import com.sharingAPI.sharing.token.TokenRepository;
import com.sharingAPI.sharing.token.TokenType;
import com.sharingAPI.sharing.user.Role;
import com.sharingAPI.sharing.user.User;
import com.sharingAPI.sharing.user.UserRepository;
import com.sharingAPI.sharing.verification.VerificationToken;
import com.sharingAPI.sharing.verification.VerificationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

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

    public AuthResponse authenticate(AuthRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        if (user.getVerified() == null) {
            VerificationToken verificationToken = verificationTokenService
                    .createVerificationToken(user.getEmail());
            var verToken = verificationToken.getToken();
            throw new IllegalArgumentException("Not verified " + verToken + " " + user.getEmail());
        }

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthResponse.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .city(user.getCity())
                .email(user.getEmail())
                .role(user.getRole())
                .token(jwtToken)
                .verification(user.getVerified().toString()).build();

    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {

        //TODO CLEANUP INVALIDATED TOKENS

        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        return LoginResponse.builder().verified(user.getVerified()).build();
    }


}
