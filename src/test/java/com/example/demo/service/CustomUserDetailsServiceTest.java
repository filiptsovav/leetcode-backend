package com.example.demo.service;

import com.example.demo.model.AppUser;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    public CustomUserDetailsServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        AppUser appUser = new AppUser("test", "pass");
        when(userRepository.findByUsername("test")).thenReturn(appUser);

        UserDetails details = service.loadUserByUsername("test");

        assertEquals("test", details.getUsername());
        assertEquals("pass", details.getPassword());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("unknown"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1", "user2", "user3"})
    void loadUserByUsername_Parameterized(String username) {
        AppUser user = new AppUser(username, "123");
        when(userRepository.findByUsername(username)).thenReturn(user);

        UserDetails details = service.loadUserByUsername(username);

        assertEquals(username, details.getUsername());
    }
}
