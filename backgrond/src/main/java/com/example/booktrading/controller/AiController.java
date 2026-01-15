package com.example.booktrading.controller;

import com.example.booktrading.entity.dto.AiChatDTO;
import com.example.booktrading.entity.vo.AiChatVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AI智能助手控制器
 */
@Tag(name = "AI智能助手", description = "AI对话、知识库查询")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {
    
    private final AiService aiService;
    
    @Operation(summary = "AI对话")
    @PostMapping("/chat")
    public Result<AiChatVO> chat(@Valid @RequestBody AiChatDTO dto) {
        AiChatVO result = aiService.chat(dto);
        return Result.success(result);
    }
    
    @Operation(summary = "刷新知识库")
    @PostMapping("/refresh-knowledge")
    public Result<Void> refreshKnowledge() {
        aiService.initKnowledgeBase();
        return Result.success();
    }
}
