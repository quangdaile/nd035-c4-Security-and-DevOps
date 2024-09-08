package com.example.demo.controller;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private final CartRepository cartRepository = mock(CartRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private MockMvc mockMvc;

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testCreateUserSuccess() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());
    }

    @Test
    public void testCreateUserFailedDueToShortPassword() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("12345");
        request.setConfirmPassword("12345");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testCreateUserFailedDueToDifferentConfirmPassword() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPas$word1");

        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void testFindUserByUserId() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("test");
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));

        ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(expectedUser.getId(), user.getId());
    }

    @Test
    public void testFindUserByUserName() {
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("test");
        when(userRepository.findByUsername("test")).thenReturn(expectedUser);

        ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(expectedUser.getUsername(), user.getUsername());
    }

    @Test
    public void testFindUserByUserNameButNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName("test");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

//    @Test
//    public void testLoginUser() throws Exception {
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setUsername("testuser");
//        loginRequest.setPassword("testpassword");
//
//        User user = new User();
//        user.setUsername("testuser");
//        user.setPassword("testpassword");
//        Mockito.when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(user);
//        Mockito.when(encoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
//        mockMvc.perform(MockMvcRequestBuilders.post("/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
////                .andExpect(content().string(token))
//        ;
//
////        CreateUserRequest createUserRequest = new CreateUserRequest();
////        createUserRequest.setUsername("testuser");
////        createUserRequest.setPassword("testpassword");
////        createUserRequest.setConfirmPassword("testpassword");
////        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/user/create")
////                        .contentType(MediaType.APPLICATION_JSON)
////                        .content(new ObjectMapper().writeValueAsString(createUserRequest)))
////                .andExpect(status().isOk());
//    }
}
