//package edu.scau.mis.common.handle;
//
//import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
//import edu.scau.mis.common.utils.SecurityUtils;
//import org.apache.ibatis.reflection.MetaObject;
//import org.springframework.stereotype.Component;
//import java.util.Date;
//import java.util.Objects;
//
//@Component
//public class MyMetaObjectHandler implements MetaObjectHandler {
//
//    @Override
//    public void insertFill(MetaObject metaObject) {
//        // 在插入时，自动填充 createTime 和 updateTime
//        this.setFieldValByName("createTime", new Date(), metaObject);
//        this.setFieldValByName("updateTime", new Date(), metaObject);
//
//        // 自动填充 createBy 和 updateBy（需要获取当前登录用户）
//        // 这里是示例，具体实现依赖于您的登录框架（如 Spring Security, Sa-Token）
//        String username = SecurityUtils.getUsername();
//        if (username != null) {
//            this.setFieldValByName("createBy", username, metaObject);
//            this.setFieldValByName("updateBy", username, metaObject);
//        } else {
//            this.setFieldValByName("createBy", "admin", metaObject);
//            this.setFieldValByName("updateBy", "admin", metaObject);
//        }
//    }
//
//    @Override
//    public void updateFill(MetaObject metaObject) {
//        // 在更新时，只自动填充 updateTime 和 updateBy
//        this.setFieldValByName("updateTime", new Date(), metaObject);
//
//        String username = SecurityUtils.getUsername();
//        this.setFieldValByName("updateBy", Objects.requireNonNullElse(username, "admin"), metaObject);
//    }
//}

package edu.scau.mis.common.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("====== MyMetaObjectHandler insertFill is running... ======"); // 增加日志，方便观察

        this.setFieldValByName("createTime", new Date(), metaObject);
        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("createBy", "admin_test", metaObject); // 使用测试值
        this.setFieldValByName("updateBy", "admin_test", metaObject); // 使用测试值
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        System.out.println("====== MyMetaObjectHandler updateFill is running... ======"); // 增加日志

        this.setFieldValByName("updateTime", new Date(), metaObject);
        this.setFieldValByName("updateBy", "admin_test_update", metaObject);
    }
}
