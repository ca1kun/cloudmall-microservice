package edu.scau.mis.product.service;

import edu.scau.mis.common.domain.Category;

import java.util.List;

public interface ICategoryService {
    Category getCategoryById(Long categoryId);

    List<Category> getAllCategories();

    boolean deleteCategoryById(Long categoryId);

    int updateCategory(Category category);

    int insertCategory(Category category);
}
