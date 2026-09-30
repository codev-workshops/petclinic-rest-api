package org.springframework.samples.petclinic.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BasicAuthenticationConfigTests {

    private static final String ADMIN_PASSWORD_HASH =
        "$2a$10$ymaklWBnpBKlgdMgkjWVF.GMGyvH8aDuTK.glFOaKw712LHtRRymS";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();
    }

    @Test
    void getVetsWithoutCredentialsRequiresBasicAuthentication() throws Exception {
        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isUnauthorized())
            .andExpect(header().string("WWW-Authenticate", startsWith("Basic")));
    }

    @Test
    void getVetsWithAdminCredentialsIsPermitted() throws Exception {
        mockMvc.perform(get("/api/vets")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin")))
            .andExpect(status().isOk());
    }

    @Test
    void getVetsWithWrongPasswordIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/vets")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "wrong")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void getVetsWithUnknownUserIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/vets")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("unknown", "admin")))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void authenticatedPostWithoutCsrfTokenReachesValidation() throws Exception {
        mockMvc.perform(post("/api/pettypes")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin"))
                .contentType("application/json")
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void passwordEncoderMatchesSeededAdminPassword() {
        assertThat(passwordEncoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(passwordEncoder.matches("admin", ADMIN_PASSWORD_HASH)).isTrue();
    }

    @Test
    void basicAuthenticationConfigurationLoadsWithoutDisabledSecurityConfiguration() {
        assertThat(context.getBeansOfType(BasicAuthenticationConfig.class)).isNotEmpty();
        assertThat(context.getBeansOfType(DisableSecurityConfig.class)).isEmpty();
    }
}
