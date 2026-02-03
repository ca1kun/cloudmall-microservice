package edu.scau.mis.product.mapper;

import edu.scau.mis.common.annotation.AutoFill;
import edu.scau.mis.common.domain.Category;


import org.springframework.context.annotation.Primary;

import java.util.List;
@Primary
public interface  ICategoryMapper{
    /**
     * 根据ID查询产品分类
     * @param categoryId
     * @return
     */
    Category selectCategoryById(Long categoryId);

    /**
     * 查询所有产品分类
     * @return
     */
    List<Category> selectAllCategoryList();

    boolean deleteCategoryById(Long categoryId);
    @AutoFill(isInsert = false)
    int updateCategory(Category category);
    @AutoFill()
    int insertCategory(Category category);

}
