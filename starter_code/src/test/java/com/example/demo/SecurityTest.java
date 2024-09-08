//package com.example.demo;
//
//import com.example.demo.controllers.UserController;
//import com.example.demo.model.requests.LoginRequest;
//import com.example.demo.model.persistence.User;
//import com.example.demo.model.persistence.repositories.UserRepository;
//import com.example.demo.security.JwtTokenProvider;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.http.MediaType;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import static org.junit.Assert.assertNotNull;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//
//@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc
//public class SecurityTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @MockBean
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//
//    @MockBean
//    private JwtTokenProvider jwtTokenProvider;
//
//    @MockBean
//    private AuthenticationManager authenticationManager;
//
//    private User user;
//    private String token;
//
//    @Before
//    public void setUp() {
//        user = new User();
//        user.setUsername("testuser");
//        user.setPassword("testpassword");
//
//        token = "test-jwt-token";
//
//        Mockito.when(userRepository.findByUsername("testuser")).thenReturn(user);
//        Mockito.when(bCryptPasswordEncoder.matches("testpassword", user.getPassword())).thenReturn(true);
//        Mockito.when(jwtTokenProvider.createToken("testuser")).thenReturn(token);
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken("testuser", "testpassword");
//        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
//                .thenReturn(authentication);
//
//        mockMvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
//    }
//
//    @Test
//    public void testLoginUser() throws Exception {
//        LoginRequest loginRequest = new LoginRequest();
//        loginRequest.setUsername("testuser");
//        loginRequest.setPassword("testpassword");
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(new ObjectMapper().writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andExpect(content().string(token));
//    }
//
//}