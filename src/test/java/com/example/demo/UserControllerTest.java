package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.AppUserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private AppUserRepository userRepo = mock(AppUserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    private Authentication authentication = mock(Authentication.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void findUserNegativeTest() {
        when(authentication.getName()).thenReturn("bobloblaw");
        ResponseEntity<AppUser> userByUsername = userController.findByUserName("notbobloblaw", authentication);
        assertEquals(403, userByUsername.getStatusCodeValue());
    }

    @Test
    public void createUserNegativeTest() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("bobloblaw");
        r.setPassword("testPassword");
        r.setConfirmPassword("incorrectPassword");

        ResponseEntity<AppUser> failedCreateUser = userController.createUser(r);
        assertEquals(400, failedCreateUser.getStatusCodeValue());
    }

    @Test
    public void findUserHappyPathTest() {
        when(authentication.getName()).thenReturn("bobloblaw");
        when(userRepo.findById(Long.valueOf(1))).thenReturn(Optional.of(TestUtils.mockedUser()));
        when(userRepo.findByUsername("bobloblaw")).thenReturn(TestUtils.mockedUser());
        // test findById
        ResponseEntity<AppUser> userById = userController.findById(Long.valueOf(1), authentication);
        assertEquals(200, userById.getStatusCodeValue());
        assertEquals("bobloblaw", userById.getBody().getUsername());
        // test findByUsername
        ResponseEntity<AppUser> userByUsername = userController.findByUserName("bobloblaw", authentication);
        assertEquals(200, userByUsername.getStatusCodeValue());
        assertEquals("bobloblaw",userByUsername.getBody().getUsername());
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
