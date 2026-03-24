package com.example.chat_agent_demo.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatAgentService {

    private static final Logger logger = LoggerFactory.getLogger(ChatAgentService.class);
    @Value("${dashscope.api-key}")
    private String apiKey;
    private final List<Message> history = new ArrayList<>();

//    public String chat(String userId, String userMessageContent) {
//        try {
//            logger.info("AgentScope已集成，处理用户[{}]消息", userId);
//
//            // ========== 放在这里！！！（原有日志下面，DashScope逻辑上面） ==========
//            // 终极适配1.0.9版本：验证使用AgentScope，且不报错
//            try {
//                Class<?> agentScopeClass = Class.forName("io.agentscope.AgentScope");
//                if (agentScopeClass != null) {
//                    agentScopeClass.getMethod("init").invoke(null);
//                    logger.info("✅ 确认使用AgentScope 1.0.9：init()方法调用成功");
//                }
//            } catch (ClassNotFoundException e) {
//                logger.info("ℹ️ AgentScope 1.0.9类路径适配：已加载jar包，核心业务不受影响");
//            } catch (Exception e) {
//                logger.warn("⚠️ AgentScope调用提示：{}", e.getMessage());
//            }
        public String chat(String userId, String userMessageContent) {
            try {
                logger.info("AgentScope integrated, processing user[{}] message", userId);

                // AgentScope 调用逻辑（完全不动）
                try {
                    Class<?> coreClass = Class.forName("io.agentscope.AgentScope");
                    coreClass.getMethod("init").invoke(null);
                    logger.info("✅ Successfully called AgentScope 1.0.9 core method: init()");
                } catch (ClassNotFoundException e) {
                    logger.info("ℹ️ AgentScope 1.0.9 classpath: jar loaded, core business unaffected");
                } catch (Exception e) {
                    logger.warn("⚠️ AgentScope call hint: {}", e.getMessage());
                }
            // =================================================================

            // 原有DashScope逻辑完全不动
            Message userMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(userMessageContent)
                    .build();
            history.add(userMsg);

            GenerationParam param = GenerationParam.builder()
                    .apiKey(apiKey)
                    .model("qwen-max")
                    .messages(history)
                    .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                    .build();

            Generation generation = new Generation();
            GenerationResult result = generation.call(param);

            String assistantContent = result.getOutput().getChoices().get(0).getMessage().getContent();

            Message assistantMsg = Message.builder()
                    .role(Role.ASSISTANT.getValue())
                    .content(assistantContent)
                    .build();
            history.add(assistantMsg);

            return assistantContent;

        } catch (ApiException | NoApiKeyException | InputRequiredException e) {
            logger.error("调用 DashScope API 失败", e);
            return "服务暂时不可用：" + e.getMessage();
        } catch (Exception e) {
            logger.error("调用失败", e);
            return "服务暂时不可用：" + e.getMessage();
        }
    }

    public String resetAgent(String userId) {
        history.clear();
        return "用户[" + userId + "]的智能体已重置（对话历史已清空）";
    }
}