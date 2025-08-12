package com.rentvideo.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rentvideo.app.model.Role;
import com.rentvideo.app.model.User;
import com.rentvideo.app.model.Video;
import com.rentvideo.app.repository.UserRepository;
import com.rentvideo.app.repository.VideoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RentVideoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private User adminUser;

    @BeforeAll
    public void setup() {
        userRepository.deleteAll();
        videoRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("testpass"));
        testUser.setRole(Role.CUSTOMER);
        userRepository.save(testUser);

        adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("adminpass"));
        adminUser.setRole(Role.ADMIN);
        userRepository.save(adminUser);
    }

    @Test
    public void testUserRegistrationSuccess() throws Exception {
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("newpass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    public void testUserRegistrationDuplicateUsername() throws Exception {
        User duplicateUser = new User();
        duplicateUser.setUsername(testUser.getUsername());
        duplicateUser.setPassword("anyPass");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Username already exists!"));
    }

    @Test
    public void testLoginEndpointReturnsSuccess() throws Exception {
        mockMvc.perform(get("/api/auth/login")
                        .header("Authorization", "Basic " + encodeBasicAuth(testUser.getUsername(), "testpass")))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful: testuser"));
    }

    @Test
    public void testLoginEndpointWithoutAuthFails() throws Exception {
        mockMvc.perform(get("/api/auth/login"))
                .andExpect(status().isUnauthorized());
    }

    // --- Video tests ---

    @Test
    public void testAddVideoAsAdmin() throws Exception {
        Video video = new Video();
        video.setTitle("Interstellar");
        video.setDirector("Christopher Nolan");
        video.setGenre("Sci-Fi");
        video.setAvailable(true);

        mockMvc.perform(post("/api/videos")
                        .header("Authorization", "Basic " + encodeBasicAuth(adminUser.getUsername(), "adminpass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(video)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Interstellar"));
    }

    @Test
    public void testAddVideoAsCustomerForbidden() throws Exception {
        Video video = new Video();
        video.setTitle("Inception");
        video.setDirector("Christopher Nolan");
        video.setGenre("Sci-Fi");
        video.setAvailable(true);

        mockMvc.perform(post("/api/videos")
                        .header("Authorization", "Basic " + encodeBasicAuth(testUser.getUsername(), "testpass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(video)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateVideoAsAdmin() throws Exception {
        // First add a video as admin
        Video video = new Video();
        video.setTitle("Old Title");
        video.setDirector("Director");
        video.setGenre("Genre");
        video.setAvailable(true);
        video = videoRepository.save(video);

        // Update details
        video.setTitle("Updated Title");
        video.setGenre("Updated Genre");

        mockMvc.perform(put("/api/videos/" + video.getId())
                        .header("Authorization", "Basic " + encodeBasicAuth(adminUser.getUsername(), "adminpass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(video)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.genre").value("Updated Genre"));
    }

    @Test
    public void testUpdateVideoAsCustomerForbidden() throws Exception {
        Video video = new Video();
        video.setTitle("Title");
        video.setDirector("Director");
        video.setGenre("Genre");
        video.setAvailable(true);
        video = videoRepository.save(video);

        video.setTitle("Customer Try Update");

        mockMvc.perform(put("/api/videos/" + video.getId())
                        .header("Authorization", "Basic " + encodeBasicAuth(testUser.getUsername(), "testpass"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(video)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteVideoAsAdmin() throws Exception {
        Video video = new Video();
        video.setTitle("To Delete");
        video.setDirector("Director");
        video.setGenre("Genre");
        video.setAvailable(true);
        video = videoRepository.save(video);

        mockMvc.perform(delete("/api/videos/" + video.getId())
                        .header("Authorization", "Basic " + encodeBasicAuth(adminUser.getUsername(), "adminpass")))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteVideoAsCustomerForbidden() throws Exception {
        Video video = new Video();
        video.setTitle("To Delete Forbidden");
        video.setDirector("Director");
        video.setGenre("Genre");
        video.setAvailable(true);
        video = videoRepository.save(video);

        mockMvc.perform(delete("/api/videos/" + video.getId())
                        .header("Authorization", "Basic " + encodeBasicAuth(testUser.getUsername(), "testpass")))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetVideosAuthenticated() throws Exception {
        mockMvc.perform(get("/api/videos")
                        .header("Authorization", "Basic " + encodeBasicAuth(testUser.getUsername(), "testpass")))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetVideosUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isUnauthorized());
    }

    // Helper to encode Basic Auth header
    private String encodeBasicAuth(String username, String password) {
        String auth = username + ":" + password;
        return java.util.Base64.getEncoder().encodeToString(auth.getBytes());
    }
}
