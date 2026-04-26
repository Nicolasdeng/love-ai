package com.loveai.chat.prompt;

/**
 * Prompt模板管理类
 */
public class PromptTemplates {

    /**
     * 系统角色设定
     */
    public static final String SYSTEM_ROLE = """
            你是一位专业的恋爱沟通顾问，具有以下特点：
            1. 深谙恋爱心理学和沟通技巧
            2. 能准确识别对方的情绪状态和潜在需求
            3. 提供的建议既温暖真诚，又符合中国人的聊天习惯
            4. 擅长用不同风格的语言表达关心（温柔、幽默、浪漫、直接）
            5. 注重实用性，避免空洞的套话
            """;

    /**
     * Few-shot示例
     */
    public static final String FEW_SHOT_EXAMPLES = """
            【示例1】
            场景：日常聊天，恋爱中，对方说"今天加班到好晚"
            分析：对方疲惫，需要关心和陪伴
            建议：
            - caring: "辛苦啦宝贝～我给你点了外卖，记得吃饭哦"
            - romantic: "再累也要想我哦，我在家等你～"
            - humorous: "加班怪兽又出现了！需要我派出爱心能量包吗？"

            【示例2】
            场景：矛盾冲突，恋爱中，对方说"你总是这样"
            分析：对方不满，需要倾听和理解
            建议：
            - caring: "对不起让你不开心了，能具体说说是哪里让你不舒服吗？"
            - direct: "我知道我有做得不好的地方，我们好好聊聊好吗？"
            """;

    /**
     * 构建完整的Prompt
     */
    public static String buildEnhancedPrompt(
            String scene,
            String relationship,
            String gender,
            String conversationHistory,
            String retrievedContext) {

        StringBuilder prompt = new StringBuilder();

        // 1. 系统角色
        prompt.append(SYSTEM_ROLE).append("\n\n");

        // 2. Few-shot示例
        prompt.append(FEW_SHOT_EXAMPLES).append("\n\n");

        // 3. RAG检索到的上下文（如果有）
        if (retrievedContext != null && !retrievedContext.isEmpty()) {
            prompt.append("【参考话术和案例】\n");
            prompt.append(retrievedContext).append("\n\n");
        }

        // 4. 当前场景信息
        prompt.append("【当前场景】\n");
        prompt.append("场景：").append(scene).append("\n");
        prompt.append("关系：").append(relationship).append("\n");
        prompt.append("对方性别：").append(gender).append("\n\n");

        // 5. 对话历史
        prompt.append("【对话历史】\n");
        prompt.append(conversationHistory).append("\n\n");

        // 6. 任务指令（使用思维链）
        prompt.append("【任务要求】\n");
        prompt.append("请按以下步骤分析并给出建议：\n");
        prompt.append("1. 情绪分析：对方当前的情绪状态是什么？\n");
        prompt.append("2. 需求识别：对方的潜在需求是什么？（倾诉、安慰、陪伴、空间等）\n");
        prompt.append("3. 回复策略：应该用什么风格和内容回复？\n");
        prompt.append("4. 生成建议：提供3-5条不同风格的回复建议\n\n");

        // 7. 输出格式
        prompt.append("请严格按照以下JSON格式返回，不要添加任何其他文字：\n");
        prompt.append("{\n");
        prompt.append("  \"suggestions\": [\n");
        prompt.append("    {\"text\": \"具体的回复内容\", \"tone\": \"friendly/romantic/humorous/caring\"},\n");
        prompt.append("    {\"text\": \"具体的回复内容\", \"tone\": \"friendly/romantic/humorous/caring\"}\n");
        prompt.append("  ],\n");
        prompt.append("  \"emotion\": {\n");
        prompt.append("    \"type\": \"happy/neutral/sad/excited/anxious\",\n");
        prompt.append("    \"intensity\": 0.5,\n");
        prompt.append("    \"description\": \"简短的情绪分析（中文）\"\n");
        prompt.append("  }\n");
        prompt.append("}\n");

        return prompt.toString();
    }
}