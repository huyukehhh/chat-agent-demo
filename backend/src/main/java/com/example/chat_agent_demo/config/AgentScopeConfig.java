package com.example.chat_agent_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

// 彻底删掉javax.annotation.PostConstruct的import，一个多余import都没有
import java.lang.reflect.Method;

@Configuration
public class AgentScopeConfig {

    @Value("${dashscope.api-key}")
    private String apiKey;

    // 静态代码块初始化AgentScope，替代@PostConstruct
    static {
        try {
            // 1.0.9版本包名是io.agentscope
            Class<?> agentScopeClass = Class.forName("io.agentscope.AgentScope");
            Method initMethod = agentScopeClass.getMethod("init");
            initMethod.invoke(null);
            System.out.println("✅ AgentScope 1.0.9 在JDK25初始化成功");
        } catch (Exception e) {
            System.err.println("⚠️ AgentScope初始化提示：" + e.getMessage());
        }
    }
}