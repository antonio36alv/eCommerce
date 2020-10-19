package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.AppUserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private AppUserRepository userRepo = mock(AppUserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        String username = "bobloblaw";
        String password = "testPassword";

        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(username);
        r.setPassword(password);
        r.setConfirmPassword(password);

        final ResponseEntity<AppUser> responseEntity = userController.createUser(r);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());

        AppUser u = responseEntity.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals(username, u.getUsername());
//        assertEquals(u.getSalt() + "thisIsHashed", u.getPassword());
    }
}
