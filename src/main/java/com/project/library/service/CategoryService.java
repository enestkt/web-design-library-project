package com.project.library.service;

import com.project.library.entity.Category;
import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category addCategory(Category category);
}
