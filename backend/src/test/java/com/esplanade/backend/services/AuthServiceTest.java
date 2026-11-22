package com.esplanade.backend.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.esplanade.backend.model.AuthenticationRequest;
import com.esplanade.backend.model.AuthenticationResponse;
import com.esplanade.backend.model.SignupRequest;
import com.esplanade.backend.model.UserData;
import com.esplanade.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void signupStoresOnlyTheEncodedPasswordAndReturnsAnAccessToken() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("bcrypt-hash");
        when(userRepository.save(any(UserData.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("signed-token");
        when(jwtService.getExpirationTime()).thenReturn(3_600_000L);

        AuthenticationResponse response = authService.signup(new SignupRequest("alice", "password123"));

        assertEquals("signed-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600, response.expiresIn());
        assertEquals("alice", response.username());
        verify(userRepository).save(any(UserData.class));
        verify(passwordEncoder).encode("password123");
    }

    @Test
    void signupRejectsDuplicateUsernames() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.signup(new SignupRequest("alice", "password123")));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(userRepository, never()).save(any(UserData.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void authenticateReturnsATokenForTheAuthenticatedUser() {
        UserDetails user = User.withUsername("alice")
                .password("bcrypt-hash")
                .authorities("ROLE_USER")
                .build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities()));
        when(jwtService.generateToken(user)).thenReturn("signed-token");
        when(jwtService.getExpirationTime()).thenReturn(3_600_000L);

        AuthenticationResponse response = authService.authenticate(
                new AuthenticationRequest("alice", "password123"));

        assertEquals("alice", response.username());
        assertEquals("signed-token", response.accessToken());
    }

    @Test
    void rejectsPasswordsThatExceedBcryptByteLimit() {
        String password = "😀".repeat(19);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> authService.signup(new SignupRequest("alice", password)));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(userRepository, never()).save(any(UserData.class));
    }
}
