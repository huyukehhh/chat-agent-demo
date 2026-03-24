package com.example.chat_agent_demo.controller;

import com.example.chat_agent_demo.service.ChatAgentService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent")
public class ChatAgentController {

    private final ChatAgentService chatAgentService;

    public ChatAgentController(ChatAgentService chatAgentService) {
        this.chatAgentService = chatAgentService;
    }

    // 原有POST接口（兼容）
    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String userId = request.getOrDefault("userId", "defaultUser");
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return "消息内容不能为空";
        }
        return chatAgentService.chat(userId, message);
    }

    // 原有GET接口（兼容）
    @GetMapping("/chat")
    public String chatGet(@RequestParam String userId, @RequestParam String message) {
        return chatAgentService.chat(userId, message);
    }

    // 新增：重置指定用户的智能体
    @PostMapping("/reset/{userId}")
    public String resetAgent(@PathVariable String userId) {
        return chatAgentService.resetAgent(userId);
    }
}