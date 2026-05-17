package ru.ochakovo.agropulse.integration.http.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import ru.ochakovo.agropulse.IntegrationTestBase;
import ru.ochakovo.agropulse.annotation.WebIT;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebIT
@RequiredArgsConstructor
class UserControllerTest extends IntegrationTestBase {

    @Autowired
    MockMvc mockMvc;

    @Test
    @WithUserDetails("admin@ochakovo.ru")
    void findAll() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("users/users"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithUserDetails("admin@ochakovo.ru")
    void create() throws Exception {
        mockMvc.perform(post("/users/create")
                        .with(csrf())
                        .param("role", "OPERATOR")
                        .param("username", "test@mail.ru")
                        .param("password", "pass1234")
                        .param("userStatus", "ACTIVE")
                        .param("lastName", "Иванова")
                        .param("firstName", "Анна")
                        .param("patronymic", "Евгеньевна"))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrlPattern("/users/{id}"));
    }
}
