package com.esplanade.backend.services;

import java.nio.charset.StandardCharsets;

import com.esplanade.backend.model.AuthenticationRequest;
import com.esplanade.backend.model.AuthenticationResponse;
import com.esplanade.backend.model.SignupRequest;
import com.esplanade.backend.model.UserData;
import com.esplanade.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final int BCRYPT_MAX_PASSWORD_BYTES = 72;

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthenticationResponse signup(SignupRequest request) {
        validatePasswordLength(request.password());
        if (userRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken");
        }

        UserData user = new UserData();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        UserData savedUser = userRepository.save(user);
        UserDetails userDetails = User.withUsername(savedUser.getUsername())
                .password(savedUser.getPassword())
                .authorities("ROLE_USER")
                .build();

        return createResponse(userDetails);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        validatePasswordLength(request.password());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        return createResponse((UserDetails) authentication.getPrincipal());
    }

    private void validatePasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > BCRYPT_MAX_PASSWORD_BYTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Password must not exceed 72 UTF-8 bytes");
        }
    }

    private AuthenticationResponse createResponse(UserDetails userDetails) {
        return new AuthenticationResponse(
                jwtService.generateToken(userDetails),
                "Bearer",
                jwtService.getExpirationTime() / 1000,
                userDetails.getUsername());
    }

}
