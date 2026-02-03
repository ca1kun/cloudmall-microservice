package edu.scau.mis.api.config;


import com.baomidou.mybatisplus.autoconfigure.MybatisPlusPropertiesCustomizer;
import edu.scau.mis.common.handle.MyMetaObjectHandler; // 导入您的处理器
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusConfig {

    /**
     * 核心改动：不再直接创建 SqlSessionFactory，
     * 而是通过 MybatisPlusPropertiesCustomizer 这个扩展点，
     * 将我们的 MetaObjectHandler 添加到 MyBatis-Plus 的全局配置中。
     * 这是新版 MP 推荐的、更优雅的集成方式。
     */
    @Bean
    public MybatisPlusPropertiesCustomizer plusPropertiesCustomizer() {
        System.out.println("====== Customizing MybatisPlusProperties... ======");
        return properties -> {
            // 将我们自己创建的 MetaObjectHandler 实例设置进去
            properties.getGlobalConfig().setMetaObjectHandler(new MyMetaObjectHandler());
            System.out.println("====== MyMetaObjectHandler has been set to GlobalConfig. ======");
        };
    }
}
