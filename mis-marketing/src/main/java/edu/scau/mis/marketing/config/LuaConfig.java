package edu.scau.mis.marketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class LuaConfig {

    @Bean
    public DefaultRedisScript<Long> seckillScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        // 指向 resources/scripts/seckill.lua
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("scripts/seckill.lua")));
        script.setResultType(Long.class); // 脚本返回类型
        return script;
    }
}