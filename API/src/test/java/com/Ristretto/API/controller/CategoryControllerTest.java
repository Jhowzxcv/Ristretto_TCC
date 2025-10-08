package com.Ristretto.API.controller;

import com.Ristretto.API.domain.category.Category;
import com.Ristretto.API.domain.category.CategoryRegistrationData;
import com.Ristretto.API.domain.category.CategoryService;
import com.Ristretto.API.domain.category.CategoryUpdateData;
import com.Ristretto.API.domain.user.User;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

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
        objectMapper = new ObjectMapper();

        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(pageableResolver)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();

        mockUser = new User();
        mockUser.setId(userId);

        mockCategory = new Category();
        mockCategory.setId(categoryId);
        mockCategory.setName("Work");
        mockCategory.setDescription("Work related tasks");
        mockCategory.setColor("#FF5733");
        mockCategory.setIcon("fa-briefcase");
        mockCategory.setUser(mockUser);

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

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreatedCategory_WhenValidDataProvided() throws Exception {
        when(service.convertToEntity(any(CategoryRegistrationData.class))).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationData)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Work"))
                .andExpect(jsonPath("$.color").value("#FF5733"));

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
        CategoryRegistrationData invalidData = new CategoryRegistrationData(
                "",
                "Description",
                "#FF5733",
                "fa-icon",
                userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("name"));

        verify(service, never()).convertToEntity(any());
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("color"));

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUserIdIsNull() throws Exception {
        CategoryRegistrationData invalidData = new CategoryRegistrationData(
                "Work",
                "Work tasks",
                "#FF5733",
                "fa-icon",
                null
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("userId"));

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldAcceptCategory_WithEmptyIcon() throws Exception {
        when(service.convertToEntity(any(CategoryRegistrationData.class))).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        CategoryRegistrationData data = new CategoryRegistrationData(
                "Work",
                "Work tasks",
                "#FF5733",
                "",
                userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated());

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldAcceptCategory_WithEmptyDescription() throws Exception {
        when(service.convertToEntity(any(CategoryRegistrationData.class))).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        CategoryRegistrationData minimalData = new CategoryRegistrationData(
                "Work",
                "",
                "#FF5733",
                "fa-icon",
                userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalData)))
                .andExpect(status().isCreated());

        verify(service, times(1)).convertToEntity(any());
        verify(service, times(1)).save(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenJsonIsEmpty() throws Exception {
        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    @Test
    void register_ShouldReturnBadRequest_WhenUserIdIsInvalidType() throws Exception {
        String json = """
            {"name":"Work","description":"Tasks","color":"#FF5733","icon":"fa-icon","userId":"abc"}
            """;

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
        verify(service, never()).convertToEntity(any());
    }
    @Test
    void register_ShouldReturnBadRequest_WhenColorIsNotHex() throws Exception {
        CategoryRegistrationData invalidData = new CategoryRegistrationData(
                "Work", "Desc", "123456", "fa-icon", userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidData)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("color"));
    }

    // ---------- LISTAGEM ----------
    @Test
    void listing_ShouldReturnPageOfCategories_WhenUserIdProvided() throws Exception {
        var page = new PageImpl<>(Collections.singletonList(mockCategory), PageRequest.of(0, 10), 1);

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
        var page = new PageImpl<Category>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    @Test
    void listing_ShouldAcceptCustomSortingParameters() throws Exception {
        var page = new PageImpl<Category>(Collections.emptyList(), PageRequest.of(0, 10), 0);

        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString()) // <-- obrigatório
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "name,desc"))
                .andExpect(status().isOk());

        verify(service, times(1)).findByUserId(any(), eq(userId));
    }

    // ---------- ATUALIZAÇÃO ----------
    @Test
    void update_ShouldReturnUpdatedCategory_WhenValidDataProvided() throws Exception {
        when(service.findByid(categoryId)).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Personal")); // valor do mockCategory

        verify(service, times(1)).findByid(categoryId);
        verify(service, times(1)).save(any());
    }

    @Test
    void update_ShouldAllowPartialUpdate_WhenOnlySomeFieldsProvided() throws Exception {
        CategoryUpdateData partialUpdate = new CategoryUpdateData(
                categoryId,
                "New Name",
                null,
                null,
                null
        );

        when(service.findByid(categoryId)).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("id"));

        verify(service, never()).findByid(any());
    }

    @Test
    void update_ShouldReturnNotFound_WhenCategoryNotFound() throws Exception {
        when(service.findByid(categoryId)).thenThrow(new com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException("Categoria não encontrada"));

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).findByid(categoryId);
        verify(service, never()).save(any());
    }

    @Test
    void update_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        String json = """
            {"id":"abc","name":"Work","description":"Tasks","color":"#FF5733","icon":"fa-icon"}
            """;

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
        verify(service, never()).findByid(any());
    }

    // ---------- DELETE ----------
    @Test
    void delete_ShouldReturnNoContent_WhenCategoryExists() throws Exception {
        when(service.existsById(categoryId)).thenReturn(true);
        doNothing().when(service).deleteById(categoryId);

        mockMvc.perform(delete("/category/{id}", categoryId))
                .andExpect(status().isNoContent());

        verify(service, times(1)).existsById(categoryId);
        verify(service, times(1)).deleteById(categoryId);
    }

    @Test
    void delete_ShouldReturnNotFound_WhenCategoryNotFound() throws Exception {
        when(service.existsById(categoryId)).thenReturn(false);

        mockMvc.perform(delete("/category/{id}", categoryId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));

        verify(service, times(1)).existsById(categoryId);
        verify(service, never()).deleteById(any());
    }

    @Test
    void delete_ShouldReturnBadRequest_WhenIdIsInvalidType() throws Exception {
        mockMvc.perform(delete("/category/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parâmetro")));

        verify(service, never()).deleteById(any());
    }
}