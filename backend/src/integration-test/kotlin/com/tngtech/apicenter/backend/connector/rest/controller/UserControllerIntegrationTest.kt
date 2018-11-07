package com.tngtech.apicenter.backend.connector.rest.controller

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun findAllSpecificationsForUser_shouldReturnSpecifications() {
        mockMvc.perform(get("/users/eb682d63-08fb-4a26-9045-1038920162ce/specifications")
            .with(user("user"))
            .with(csrf()))
            .andExpect(jsonPath("$[0].title", equalTo("Spec1")))
            .andExpect(jsonPath("$[1].title", equalTo("Spec2")))
    }

}