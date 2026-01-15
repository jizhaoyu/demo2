package com.example.booktrading.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.booktrading.entity.vo.BookVO;
import com.example.booktrading.entity.vo.UserVO;

import java.util.Map;

/**
 * 管理员服务接口
 */
public interface AdminService {
    
    /**
     * 获取用户列表（分页）
     */
    IPage<UserVO> getUserList(Integer page, Integer size, String keyword, Integer status);
    
    /**
     * 更新用户状态（启用/禁用）
     */
    void updateUserStatus(Long userId, Integer status);
    
    /**
     * 获取系统统计数据
     */
    Map<String, Object> getStatistics();
    
    /**
     * 获取书籍列表（管理员视角，可查看所有书籍）
     */
    IPage<BookVO> getBookList(Integer page, Integer size, String keyword, Integer status);
    
    /**
     * 删除书籍（管理员强制删除）
     */
    void deleteBook(Long bookId);
}
