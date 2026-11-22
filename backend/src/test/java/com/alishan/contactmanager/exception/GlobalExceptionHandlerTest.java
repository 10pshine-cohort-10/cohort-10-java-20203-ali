package com.alishan.contactmanager.exception;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(new TestController())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    @RestController
    static class TestController {

        @GetMapping("/test/missing")
        public void missingContact() {
            throw new ResourceNotFoundException("Contact not found");
        }

        @GetMapping("/test/duplicate")
        public void duplicateResource() {
            throw new DuplicateResourceException("Email already registered");
        }

        @PostMapping("/test/contacts")
        public void createContact(@Valid @RequestBody TestContactRequest request) {
        }

        @GetMapping("/test/unexpected")
        public void unexpectedError() {
            throw new IllegalStateException("Internal diagnostic detail");
        }

    }

    @Test
    void missingContactReturns404WithApiError() throws Exception {
        mockMvc.perform(get("/test/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Contact not found"))
                .andExpect(jsonPath("$.path").value("/test/missing"))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void duplicateResourceReturns409WithApiError() throws Exception {
        mockMvc.perform(get("/test/duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Email already registered"))
                .andExpect(jsonPath("$.path").value("/test/duplicate"))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    record TestContactRequest(
            @NotBlank(message = "First name is required") String firstName) {
    }

    @Test
    void blankFirstNameReturns400WithFieldError() throws Exception {
        mockMvc.perform(post("/test/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors.firstName").value("First name is required"));
    }

    @Test
    void unexpectedErrorReturns500WithGenericMessage() throws Exception {
        mockMvc.perform(get("/test/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred."))
                .andExpect(jsonPath("$.path").value("/test/unexpected"))
                .andExpect(jsonPath("$.fieldErrors").isMap())
                .andExpect(jsonPath("$.fieldErrors").isEmpty());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/test/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"firstName\":"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request body is missing or invalid."));
    }
}