package com.Ristretto.API.domain.categories;

import com.Ristretto.API.domain.user.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository repository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CategoryService categoryService;

    private User mockUser;
    private Category mockCategory;
    private CategoryRegistrationData completeCategoryData;
    private CategoryRegistrationData minimalCategoryData;
    private final Long categoryId = 1L;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockCategory = new Category();

        ReflectionTestUtils.setField(categoryService, "entityManager", entityManager);
        lenient().when(entityManager.getReference(User.class, userId)).thenReturn(mockUser);

        completeCategoryData = new CategoryRegistrationData(
                "Work Category",
                "Category for work-related tasks",
                "#FF5733",
                "work-icon",
                userId
        );

        minimalCategoryData = new CategoryRegistrationData(
                "Personal Category",
                null,
                null,
                null,
                userId
        );
    }

    @Test
    void findByUserId_ShouldReturnPageOfCategories_WhenUserIdProvided() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Category> expectedPage = new PageImpl<>(Arrays.asList(mockCategory));
        when(repository.findByUserId(userId, pageable)).thenReturn(expectedPage);

        Page<Category> result = categoryService.findByUserId(pageable, userId);

        assertEquals(expectedPage, result);
        verify(repository, times(1)).findByUserId(userId, pageable);
    }

    @Test
    void convertToEntity_ShouldCreateCompleteCategory_WhenCompleteDataProvided() {
        Category result = categoryService.convertToEntity(completeCategoryData);

        assertEquals(completeCategoryData.name(), result.getName());
        assertEquals(completeCategoryData.description(), result.getDescription());
        assertEquals(completeCategoryData.color(), result.getColor());
        assertEquals(completeCategoryData.icon(), result.getIcon());
        assertEquals(mockUser, result.getUser());

        verify(entityManager, times(1)).getReference(User.class, userId);
    }

    @Test
    void convertToEntity_ShouldCreateMinimalCategory_WhenMinimalDataProvided() {
        Category result = categoryService.convertToEntity(minimalCategoryData);

        assertEquals(minimalCategoryData.name(), result.getName());
        assertEquals(minimalCategoryData.description(), result.getDescription());
        assertEquals(minimalCategoryData.color(), result.getColor());
        assertEquals(minimalCategoryData.icon(), result.getIcon());
        assertEquals(mockUser, result.getUser());

        verify(entityManager, times(1)).getReference(User.class, userId);
    }

    @Test
    void save_ShouldReturnSavedCategory_WhenCategoryProvided() {
        when(repository.save(mockCategory)).thenReturn(mockCategory);

        Category result = categoryService.save(mockCategory);

        assertEquals(mockCategory, result);
        verify(repository, times(1)).save(mockCategory);
    }

    @Test
    void deleteById_ShouldCallRepositoryDelete_WhenIdProvided() {
        categoryService.deleteById(categoryId);

        verify(repository, times(1)).deleteById(categoryId);
    }
}