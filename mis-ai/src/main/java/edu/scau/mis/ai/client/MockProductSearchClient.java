package edu.scau.mis.ai.client;

import edu.scau.mis.ai.dto.ProductSearchCondition;
import edu.scau.mis.ai.vo.ProductCandidateVo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class MockProductSearchClient {

    public List<ProductCandidateVo> searchProducts(ProductSearchCondition condition) {
        List<ProductCandidateVo> list = new ArrayList<>();

        ProductCandidateVo p1 = new ProductCandidateVo();
        p1.setProductId("1001");
        p1.setProductName("学生党高续航蓝牙耳机 A1");
        p1.setCategoryName("耳机");
        p1.setPrice(new BigDecimal("299"));
        p1.setStock(100);
        p1.setSellingPoint("价格低、续航长、适合学生日常通勤");
        p1.setDescription("适合学生使用的入门蓝牙耳机，续航较好，佩戴轻便。");
        p1.setImageUrl("https://example.com/1001.jpg");
        list.add(p1);

        ProductCandidateVo p2 = new ProductCandidateVo();
        p2.setProductId("1002");
        p2.setProductName("降噪蓝牙耳机 B2");
        p2.setCategoryName("耳机");
        p2.setPrice(new BigDecimal("499"));
        p2.setStock(50);
        p2.setSellingPoint("支持降噪、续航优秀、性价比较高");
        p2.setDescription("适合学习、自习室、通勤场景，支持基础降噪。");
        p2.setImageUrl("https://example.com/1002.jpg");
        list.add(p2);

        ProductCandidateVo p3 = new ProductCandidateVo();
        p3.setProductId("1003");
        p3.setProductName("运动蓝牙耳机 C3");
        p3.setCategoryName("耳机");
        p3.setPrice(new BigDecimal("399"));
        p3.setStock(80);
        p3.setSellingPoint("佩戴稳定、防汗、适合运动");
        p3.setDescription("适合跑步和健身使用，佩戴稳定。");
        p3.setImageUrl("https://example.com/1003.jpg");
        list.add(p3);

        return list;
    }
}