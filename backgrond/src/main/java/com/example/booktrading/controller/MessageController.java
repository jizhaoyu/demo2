package com.example.booktrading.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.dto.MessageDTO;
import com.example.booktrading.entity.vo.ConversationVO;
import com.example.booktrading.entity.vo.MessageVO;
import com.example.booktrading.entity.vo.Result;
import com.example.booktrading.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 消息控制器
 */
@Tag(name = "消息管理", description = "站内消息功能")
@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @Operation(summary = "获取会话列表")
    @GetMapping("/conversations")
    public Result<List<ConversationVO>> getConversations() {
        List<ConversationVO> conversations = messageService.getConversations();
        return Result.success(conversations);
    }
    
    @Operation(summary = "获取与某用户的聊天记录")
    @GetMapping("/history/{userId}")
    public Result<IPage<MessageVO>> getHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        IPage<MessageVO> pageResult = messageService.getHistory(userId, page, size);
        return Result.success(pageResult);
    }
    
    @Operation(summary = "发送消息")
    @PostMapping
    public Result<Void> send(@Valid @RequestBody MessageDTO dto) {
        messageService.send(dto);
        return Result.success();
    }
    
    @Operation(summary = "标记消息为已读")
    @PutMapping("/read/{userId}")
    public Result<Void> markAsRead(@PathVariable Long userId) {
        messageService.markAsRead(userId);
        return Result.success();
    }
    
    @Operation(summary = "删除会话")
    @DeleteMapping("/conversation/{userId}")
    public Result<Void> deleteConversation(@PathVariable Long userId) {
        messageService.deleteConversation(userId);
        return Result.success();
    }
    
    @Operation(summary = "获取未读消息数")
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        int count = messageService.getUnreadCount();
        return Result.success(count);
    }
}
