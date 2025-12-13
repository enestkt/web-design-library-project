package com.project.library.controller;

import com.project.library.dto.category.CategoryRequestDto;
import com.project.library.dto.category.CategoryResponseDto;
import com.project.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponseDto> getAll() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public CategoryResponseDto getById(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @PostMapping
    public CategoryResponseDto create(@RequestBody CategoryRequestDto dto) {
        return categoryService.createCategory(dto);
    }

    @PutMapping("/{id}")
    public CategoryResponseDto update(@PathVariable Long id, @RequestBody CategoryRequestDto dto) {
        return categoryService.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
