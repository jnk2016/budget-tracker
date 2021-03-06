package com.jaxnk2020.budgettracker.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    HashMap<String, String> body;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        body = new HashMap<>();
        body.put("username", "bioround");
        body.put("password", "paypal123");
        body.put("firstname", "Nikhil");
        body.put("lastname", "Suri");

    }

    @Test
    void shouldSignUp() {
        when(userService.newUser(body)).thenReturn(true);

        assertEquals(200, userController.signUp(body).getStatusCodeValue());
    }

    @Test
    void shouldNOTSignUpWhenSameUsername() {
        when(userService.newUser(body)).thenReturn(false);

        assertEquals(400, userController.signUp(body).getStatusCodeValue());
    }

    @Test
    void shouldNOTSignUpWhenWrongBody() {
        body.put("DateJoined", "4/20/2021");
        body.remove("lastname");

        assertEquals(400, userController.signUp(body).getStatusCodeValue());
    }

    @Test
    void shouldReturnDateJoined() {
        when(userService.getDateJoined(any(Authentication.class))).thenReturn(LocalDate.now());
        Authentication auth = Mockito.mock(Authentication.class);
        assertEquals(200, userController.dateJoined(auth).getStatusCodeValue());
    }

    @Test
    void shouldNOTReturnDateJoinedWhenBadAuth() {
        when(userService.getDateJoined(any(Authentication.class))).thenReturn(null);
        assertEquals(400, userController.dateJoined(any(Authentication.class)).getStatusCodeValue());
    }
}