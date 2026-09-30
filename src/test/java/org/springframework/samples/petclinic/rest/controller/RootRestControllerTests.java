package org.springframework.samples.petclinic.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.samples.petclinic.rest.advice.ExceptionControllerAdvice;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RootRestControllerTests {

    @Test
    void testRedirectsToSwaggerUsingServletContextPath() throws Exception {
        RootRestController controller = new RootRestController();
        ReflectionTestUtils.setField(controller, "servletContextPath", "/petclinic");
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/petclinic/swagger-ui/index.html"));
    }

    @Test
    void testRedirectsToSwaggerWithoutServletContextPath() throws Exception {
        RootRestController controller = new RootRestController();
        ReflectionTestUtils.setField(controller, "servletContextPath", "");
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new ExceptionControllerAdvice())
            .build();

        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/swagger-ui/index.html"));
    }
}
