//package edu.scau.mis.pos.service.impl;
//
//import edu.scau.mis.pos.PosTestApplication;
//import edu.scau.mis.common.domain.Product;
//import edu.scau.mis.pos.service.IProductService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//@SpringBootTest(classes = {PosTestApplication.class})
////@Import(ProductServiceImpl.class)  // 解决方案1：显式导入
//class ProductServiceImplTest {
//
//    @Autowired
//    private IProductService productService;
//
//    @MockBean
//    private com.aliyun.oss.OSS ossClient;  // Mock掉OSS客户端
//
//    @Test
//    void getProductById() {
//        Product product=productService.getProductById(1L);
//        System.out.println("product = " + product);
//    }
//
//    @Test
//    void getProductBySn() {
//    }
//
//    @Test
//    void getAllProducts() {
//    }
//
//    @Test
//    void getProducts() {
//    }
//
//    @Test
//    void addProduct() {
//    }
//
//    @Test
//    void updateProduct() {
//    }
//
//    @Test
//    void deleteProduct() {
//    }
//}