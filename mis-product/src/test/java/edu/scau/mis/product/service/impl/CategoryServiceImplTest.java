//package edu.scau.mis.pos.service.impl;
//
//import edu.scau.mis.pos.PosTestApplication;
//import edu.scau.mis.common.domain.Category;
//import edu.scau.mis.pos.mapper.ICategoryMapper;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import static org.junit.jupiter.api.Assertions.*;
//
////@SpringBootTest(classes = {PosTestApplication.class})
//class CategoryServiceImplTest {
//
//    @Autowired
//    private ICategoryMapper categoryMapper;
//    @MockBean
//    private com.aliyun.oss.OSS ossClient;  // Mock掉OSS客户端
//
//    @Test
//    void getCategoryById() {
//        Category category = categoryMapper.selectCategoryById(1L);
//        System.out.println("category = " + category);
//
//    }
//
//    @Test
//    void getAllCategories() {
//    }
//
//    @Test
//    void deleteCategoryById() {
//    }
//
//    @Test
//    void updateCategory() {
//    }
//
//    @Test
//    void insertCategory() {
//    }
//}