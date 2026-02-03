//package edu.scau.mis.pos.mapper;
//
//import edu.scau.mis.common.domain.Category;
//import edu.scau.mis.pos.mapper.ICategoryMapper;
//import edu.scau.mis.web.Main;
//import org.junit.jupiter.api.Test;
//import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
//import org.redisson.api.RedissonClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.annotation.Rollback;
//import org.springframework.test.context.ContextConfiguration;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@MybatisTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
//@EnableAutoConfiguration(exclude = {
//        RedisAutoConfiguration.class,
//        RedisRepositoriesAutoConfiguration.class
//})
//@ContextConfiguration(classes = Main.class)
//public class ICategoryMapperTest {
//
//    @Autowired
//    private ICategoryMapper categoryMapper;
//
//    @MockBean
//    private RedissonClient redissonClient;
//
//    @MockBean
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Test
//    void selectCategoryById() {
//        Category category = categoryMapper.selectCategoryById(1L);
//        assertEquals("文具", category.getCategoryName());
//    }
//
//    @Test
//    void selectAllCategoryList() {
//        List<Category> categories = categoryMapper.selectAllCategoryList();
//        assertEquals(3, categories.size());
//    }
//    @Test
//    void insertCategory() {
//        Category category = new Category();
//        category.setCategoryName("电子产品");
//        category.setParentId(0L);
//        category.setCreateBy("test");
//
//        categoryMapper.insertCategory(category);
//        assertEquals("电子产品", categoryMapper.selectCategoryById(category.getCategoryId()).getCategoryName());
//    }
//
//    @Test
//    void updateCategory() {
//        Category category = new Category();
//        category.setCategoryName("测试分类");
//        category.setParentId(0L);
//        category.setCreateBy("test");
//        categoryMapper.insertCategory(category);
//
//        Long categoryId = category.getCategoryId();
//        category.setCategoryName("更新后的分类名");
//        categoryMapper.updateCategory(category);
//
//        assertEquals("更新后的分类名", categoryMapper.selectCategoryById(categoryId).getCategoryName());
//    }
//
//    @Test
//    void deleteCategoryById() {
//        // 先插入一个测试分类
//        Category category = new Category();
//        category.setCategoryName("待删除分类");
//        category.setParentId(0L);
//        category.setCreateBy("test");
//        categoryMapper.insertCategory(category);
//
//        Long categoryId = category.getCategoryId();
//        categoryMapper.deleteCategoryById(categoryId);
//
//        assertNull(categoryMapper.selectCategoryById(categoryId));
//    }
//
//
//}