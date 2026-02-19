package com.zest.product.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zest.product.management.dto.ProductDto;
import com.zest.product.management.entity.Role;
import com.zest.product.management.entity.User;
import com.zest.product.management.repository.RoleRepository;
import com.zest.product.management.repository.UserRepository;
import com.zest.product.management.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(null, Role.RoleName.ROLE_ADMIN)));
        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseGet(() -> roleRepository.save(new Role(null, Role.RoleName.ROLE_USER)));

        // Create Admin
        User admin = User.builder()
                .username("admin_test")
                .email("admin@test.com")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(adminRole))
                .build();
        userRepository.save(admin);

        // Create User
        User user = User.builder()
                .username("user_test")
                .email("user@test.com")
                .password(passwordEncoder.encode("password"))
                .roles(Set.of(userRole))
                .build();
        userRepository.save(user);

        adminToken = generateToken("admin_test", "password");
        userToken = generateToken("user_test", "password");
    }

    private String generateToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        return jwtUtils.generateJwtToken(authentication);
    }

    @Test
    void getAllProducts_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/products")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Products fetched successfully"));
    }

    @Test
    void createProduct_AsAdmin_ShouldReturnSuccess() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Test Integration Product");

        mockMvc.perform(post("/api/v1/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productName").value("Test Integration Product"));
    }

    @Test
    void createProduct_AsUser_ShouldReturnForbidden() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Test Illegal Product");

        mockMvc.perform(post("/api/v1/products")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteProduct_AsAdmin_ShouldReturnSuccess() throws Exception {
        // First create
        ProductDto productDto = new ProductDto();
        productDto.setProductName("Delete Me");

        String response = mockMvc.perform(post("/api/v1/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andReturn().getResponse().getContentAsString();

        Integer id = objectMapper.readTree(response).get("data").get("id").asInt();

        mockMvc.perform(delete("/api/v1/products/" + id)
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));
    }

    @Test
    void getProductById_WhenNotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/products/999")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturn400() throws Exception {
        ProductDto productDto = new ProductDto();
        productDto.setProductName(""); // Invalid: NotBlank

        mockMvc.perform(post("/api/v1/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void optionsRequest_ShouldReturnCorsHeaders() throws Exception {
        mockMvc.perform(options("/api/v1/products")
                .header("Origin", "http://example.com")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "*"))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }
}
