package com.example.booktrading.entity.vo;

import lombok.Data;

/**
 * AI对话响应VO
 */
@Data
public class AiChatVO {
    
    /**
     * AI回复内容
     */
    private String reply;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 是否成功
     */
    private Boolean success;
    
    /**
     * 错误信息
     */
    private String error;
    
    public static AiChatVO success(String reply, String sessionId) {
        AiChatVO vo = new AiChatVO();
        vo.setReply(reply);
        vo.setSessionId(sessionId);
        vo.setSuccess(true);
        return vo;
    }
    
    public static AiChatVO error(String error) {
        AiChatVO vo = new AiChatVO();
        vo.setSuccess(false);
        vo.setError(error);
        return vo;
    }
}
