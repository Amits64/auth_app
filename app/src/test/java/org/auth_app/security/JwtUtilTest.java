package org.auth_app.security;
import org.auth_app.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGenerateToken() {
        // Example test case
        assertNotNull(jwtUtil);
        // Add your test logic here
    }
}
