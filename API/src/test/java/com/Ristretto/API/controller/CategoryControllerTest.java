package com.Ristretto.API.controller;

import com.Ristretto.API.domain.category.*;
import com.Ristretto.API.domain.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService service;

    @InjectMocks
    private CategoryController controller;

    private Category mockCategory;
    private User mockUser;
    private CategoryRegistrationData registrationData;
    private CategoryUpdateData updateData;
    private final Long categoryId = 1L;
    private final Long userId = 1L;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        mockUser = new User();
        mockCategory = new Category();

        registrationData = new CategoryRegistrationData(
                "Work",
                "Work related tasks",
                "#FF5733",
                "fa-briefcase",
                userId
        );

        updateData = new CategoryUpdateData(
                categoryId,
                "Personal",
                "Personal tasks",
                "#33FF57",
                "fa-home"
        );
    }

    @Test
    void register_ShouldReturnCreatedCategory_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(CategoryRegistrationData.class))).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidDataProvided() throws Exception {
        CategoryRegistrationData invalidData = new CategoryRegistrationData(
                "", // nome vazio
                "Description",
                "#FF5733",
                "fa-icon",
                userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void listing_ShouldReturnPageOfCategories_WhenUserIdProvided() throws Exception {
        var categories = Arrays.asList(mockCategory);
        var page = new PageImpl<>(categories, PageRequest.of(0, 10), 1);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoCategoriesFound() throws Exception {
        var page = new PageImpl<Category>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void update_ShouldReturnUpdatedCategory_WhenValidDataProvided() throws Exception {
        when(service.findByid(categoryId)).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(service, times(1)).findByid(categoryId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsNull() throws Exception {
        CategoryUpdateData invalidData = new CategoryUpdateData(
                null,
                "Personal",
                "Personal tasks",
                "#33FF57",
                "fa-home"
        );

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findByid(any());
    }

    @Test
    void delete_ShouldReturnNoContent_WhenCategoryExists() throws Exception {
        doNothing().when(service).deleteById(categoryId);

        mockMvc.perform(delete("/category/{id}", categoryId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteById(categoryId);
    }

    @Test
    void delete_ShouldHandleException_WhenCategoryNotFound() throws Exception {
        doThrow(new RuntimeException("Category not found")).when(service).deleteById(categoryId);

        mockMvc.perform(delete("/category/{id}", categoryId))
                .andExpect(status().isInternalServerError());

        verify(service, times(1)).deleteById(categoryId);
    }

    @Test
    void register_ShouldReturnBadRequest_WhenColorFormatIsInvalid() throws Exception {
        CategoryRegistrationData invalidColorData = new CategoryRegistrationData(
                "Work",
                "Work tasks",
                "INVALID_COLOR",
                "fa-icon",
                userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidColorData)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }
}
