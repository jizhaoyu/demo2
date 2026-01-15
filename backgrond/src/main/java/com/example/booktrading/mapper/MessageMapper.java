package com.example.booktrading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.booktrading.entity.po.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 消息Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    
    /**
     * 统计未读消息数
     */
    @Select("SELECT COUNT(*) FROM message WHERE receiver_id = #{userId} AND is_read = 0 AND deleted_by_receiver = 0")
    int countUnread(@Param("userId") Long userId);
    
    /**
     * 标记与某用户的消息为已读
     */
    @Update("UPDATE message SET is_read = 1 WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    int markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}
