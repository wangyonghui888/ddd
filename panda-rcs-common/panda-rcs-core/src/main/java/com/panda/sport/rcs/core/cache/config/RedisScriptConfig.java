package com.panda.sport.rcs.core.cache.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scripting.support.ResourceScriptSource;

import com.panda.sport.rcs.core.bean.RedisScriptBean;

@Configuration
public class RedisScriptConfig {

    private final static String SCRIPT_KEY_LOCK = "script_key_lock";
    private final static String SCRIPT_KEY_UNLOCK = "script_key_unlock";
    /**
     * @Description   increase脚本 
     * @Param 
     * @Author toney
     * @Date  11:34 2019/10/12
     * @return 
     **/
    private final static String SCRIPT_KEY_INCREASE = "increase";

    @Bean(name = "lock")
    public RedisScriptBean<Boolean> lockScript() throws IOException {
        RedisScriptBean<Boolean> redisScript = new RedisScriptBean<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("/scripts/lua/lock.lua")).getScriptAsString());
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptKey(SCRIPT_KEY_LOCK);
        return redisScript;
    }

    @Bean(name = "unlock")
    public RedisScriptBean<Boolean> unlockScript() throws IOException {
        RedisScriptBean<Boolean> redisScript = new RedisScriptBean<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("/scripts/lua/unlock.lua")).getScriptAsString());
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptKey(SCRIPT_KEY_UNLOCK);
        return redisScript;
    }
    /*
     * @Description   加载increase bean
     * @Param []
     * @Author toney
     * @Date  11:36 2019/10/12
     * @return org.springframework.data.redis.core.script.RedisScript<java.lang.Boolean>
     **/
    @Bean(name = "incr_expire_1")
    public RedisScriptBean<Boolean> increaseScript() throws IOException {
        RedisScriptBean<Boolean> redisScript = new RedisScriptBean<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("/scripts/lua/incr_expire_1.lua")).getScriptAsString());
        redisScript.setResultType(Boolean.class);
        redisScript.setScriptKey(SCRIPT_KEY_INCREASE);
        return redisScript;
    }
}
