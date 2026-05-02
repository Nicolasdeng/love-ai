package com.loveai.chat.service.impl;

import com.loveai.chat.dto.SimulateRequest;
import com.loveai.chat.dto.SimulateResponse;
import com.loveai.chat.prompt.PromptTemplates;
import com.loveai.chat.service.ChatSimulateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSimulateServiceImpl implements ChatSimulateService {

    private final ChatClient chatClient;

    @Override
    public SimulateResponse simulate(SimulateRequest request) {
        String systemPrompt = PromptTemplates.buildSimulateSystemPrompt(
                getScenarioDescription(request.getScenario()),
                request.getTargetGender() != null ? request.getTargetGender() : "女",
                request.getRelationship() != null ? request.getRelationship() : "暧昧",
                request.getTargetPersonality()
        );

        // 构建多轮消息历史
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));

        if (request.getHistory() != null) {
            for (SimulateRequest.Message msg : request.getHistory()) {
                if ("user".equals(msg.getRole())) {
                    messages.add(new UserMessage(msg.getContent()));
                } else {
                    messages.add(new AssistantMessage(msg.getContent()));
                }
            }
        }
        messages.add(new UserMessage(request.getUserMessage()));

        log.info("情境模拟，场景: {}, 历史轮数: {}", request.getScenario(),
                request.getHistory() == null ? 0 : request.getHistory().size());

        String rawReply = chatClient.prompt(new Prompt(messages))
                .call()
                .content();

        return parseReply(rawReply);
    }

    /**
     * 解析AI回复，分离正文与反馈
     */
    private SimulateResponse parseReply(String rawReply) {
        SimulateResponse response = new SimulateResponse();
        if (rawReply.contains("【反馈】")) {
            int idx = rawReply.lastIndexOf("【反馈】");
            response.setReply(rawReply.substring(0, idx).trim());
            String feedbackPart = rawReply.substring(idx + 4).trim();
            // 尝试从反馈里提取评分（例如："表达自然，情感到位（90分）"）
            response.setFeedback(feedbackPart.replaceAll("（\\d+分）", "").trim());
            response.setScore(extractScore(feedbackPart));
        } else {
            response.setReply(rawReply.trim());
            response.setScore(70);
        }
        return response;
    }

    private Integer extractScore(String feedback) {
        try {
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("（(\\d+)分）")
                    .matcher(feedback);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } catch (Exception ignored) {
        }
        return 70;
    }

    private String getScenarioDescription(String scenario) {
        return switch (scenario) {
            case "confession" -> "初次表白";
            case "date_invite" -> "约会邀请";
            case "conflict" -> "矛盾化解";
            case "comfort" -> "情绪安抚";
            case "surprise" -> "制造惊喜";
            default -> scenario;
        };
    }
}