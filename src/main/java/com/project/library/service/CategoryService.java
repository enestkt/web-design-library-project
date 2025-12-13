package com.project.library.service;

import com.project.library.dto.category.CategoryRequestDto;
import com.project.library.dto.category.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto dto);

    CategoryResponseDto getCategory(Long id);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto);

    void deleteCategory(Long id);
}
