package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "petclinic.security.enable=false")
class DisableSecurityConfigTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    void disabledSecurityConfigurationLoadsWithoutBasicAuthenticationBeans() {
        assertThat(context.getBeansOfType(DisableSecurityConfig.class)).isNotEmpty();
        assertThat(context.getBeansOfType(BasicAuthenticationConfig.class)).isEmpty();
        assertThat(context.getBeansOfType(PasswordEncoder.class)).isEmpty();
    }

    @Test
    void getVetsWithoutCredentialsIsPermitted() throws Exception {
        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk());
    }

    @Test
    void responseAllowsSameOriginFrames() throws Exception {
        mockMvc.perform(get("/api/vets"))
            .andExpect(header().string("X-Frame-Options", "SAMEORIGIN"));
    }

    @Test
    void postWithoutCredentialsOrCsrfTokenReachesValidation() throws Exception {
        mockMvc.perform(post("/api/pettypes")
                .contentType("application/json")
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
