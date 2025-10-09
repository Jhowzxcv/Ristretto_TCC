package com.Ristretto.API.controller;

import com.Ristretto.API.domain.category.*;
import com.Ristretto.API.infra.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

@DisplayName("Category Controller Tests (Completo Revisado)")
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService service;

    @InjectMocks
    private CategoryController controller;

    private ObjectMapper objectMapper;
    private final Long userId = 1L;
    private final Long categoryId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    // ---------- REGISTRO ----------
    @Test
    void register_ShouldReturnCreated_WhenValidData() throws Exception {
        Category mockCategory = new Category();
        mockCategory.setId(categoryId);
        mockCategory.setName("Work");
        mockCategory.setColor("#FF5733");

        when(service.convertToEntity(any(CategoryRegistrationData.class))).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        CategoryRegistrationData data = new CategoryRegistrationData(
                "Work", "Desc", "#FF5733", "fa-icon", userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Work"));
    }

    @ParameterizedTest(name = "Registro inválido → nome: \"{0}\" | cor: \"{1}\" | userId: {2}")
    @CsvSource({
            "'', #FF5733, 1",        // nome vazio
            "Work, INVALID, 1",      // cor inválida
            "Work, 123456, 1",       // cor sem #
            "Work, #FF5733, "        // userId nulo
    })
    void register_ShouldReturnBadRequest_WhenInvalidFields(String name, String color, Long userId) throws Exception {
        CategoryRegistrationData invalid = new CategoryRegistrationData(
                name, "Desc", color, "fa-icon", userId
        );

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
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
    void register_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"name\":\"Work\",\"color\":}";

        mockMvc.perform(post("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).convertToEntity(any());
    }

    // ---------- LISTAGEM ----------
    @Test
    void listing_ShouldReturnPageOfCategories_WhenUserIdProvided() throws Exception {
        Category mockCategory = new Category();
        mockCategory.setId(categoryId);
        mockCategory.setName("Work");

        var page = new PageImpl<>(Collections.singletonList(mockCategory), PageRequest.of(0, 10), 1);
        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listing_ShouldReturnEmptyPage_WhenNoCategoriesFound() throws Exception {
        var page = new PageImpl<Category>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(service.findByUserId(any(), eq(userId))).thenReturn(page);

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listing_ShouldReturnBadRequest_WhenPaginationInvalid() throws Exception {
        when(service.findByUserId(any(), any())).thenReturn(new PageImpl<>(Collections.emptyList()));

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString())
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/category")
                        .param("userId", userId.toString())
                        .param("page", "0")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }

    // ---------- ATUALIZAÇÃO ----------
    @Test
    void update_ShouldReturnOk_WhenValidData() throws Exception {
        Category mockCategory = new Category();
        mockCategory.setId(categoryId);
        mockCategory.setName("Personal");

        when(service.findByid(categoryId)).thenReturn(mockCategory);
        when(service.save(any(Category.class))).thenReturn(mockCategory);

        CategoryUpdateData update = new CategoryUpdateData(categoryId, "Personal", "Desc", "#33FF57", "fa-home");

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(categoryId))
                .andExpect(jsonPath("$.name").value("Personal"));
    }

    @ParameterizedTest(name = "Update inválido com id = {0}")
    @CsvSource({",", "-1"})
    void update_ShouldReturnBadRequest_WhenIdInvalid(Long id) throws Exception {
        CategoryUpdateData invalid = new CategoryUpdateData(id, "Name", "Desc", "#FF5733", "fa-icon");

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());

        verify(service, never()).findByid(any());
    }

    @Test
    void update_ShouldReturnNotFound_WhenCategoryNotFound() throws Exception {
        when(service.findByid(categoryId))
                .thenThrow(new com.Ristretto.API.infra.exception.Domain.ResourceNotFoundException("Categoria não encontrada"));

        CategoryUpdateData update = new CategoryUpdateData(categoryId, "Personal", "Desc", "#33FF57", "fa-home");

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("\"Recurso não encontrado\""));
    }

    @Test
    void update_ShouldReturnBadRequest_WhenJsonMalformed() throws Exception {
        String malformedJson = "{\"id\":1,\"name\":\"Personal\",\"color\":}";

        mockMvc.perform(put("/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any());
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
    void delete_ShouldReturnBadRequest_WhenIdInvalidType() throws Exception {
        mockMvc.perform(delete("/category/{id}", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Parâmetro")));

        verify(service, never()).deleteById(any());
    }
}