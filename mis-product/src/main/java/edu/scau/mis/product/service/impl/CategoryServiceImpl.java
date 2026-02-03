package edu.scau.mis.product.service.impl;

import edu.scau.mis.common.domain.Category;
import edu.scau.mis.product.mapper.ICategoryMapper;
import edu.scau.mis.product.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private ICategoryMapper categoryMapper;

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryMapper.selectCategoryById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryMapper.selectAllCategoryList();
    }

    @Override
    public boolean deleteCategoryById(Long categoryId) {
        return categoryMapper.deleteCategoryById(categoryId);
    }

    @Override
    public int updateCategory(Category category){
        return categoryMapper.updateCategory(category);
    }

    @Override
    public int insertCategory(Category category){
        return categoryMapper.insertCategory(category);
    }


}
