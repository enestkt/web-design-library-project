package com.project.library.service.impl;

import com.project.library.dto.category.CategoryRequestDto;
import com.project.library.dto.category.CategoryResponseDto;
import com.project.library.entity.Category;
import com.project.library.repository.CategoryRepository;
import com.project.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        categoryRepository.save(category);

        return convertToDto(category);
    }

    @Override
    public CategoryResponseDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return convertToDto(category);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(dto.getName());
        categoryRepository.save(category);

        return convertToDto(category);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    private CategoryResponseDto convertToDto(Category category) {
        CategoryResponseDto dto = new CategoryResponseDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
