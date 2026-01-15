package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.po.Book;
import com.example.booktrading.entity.po.Orders;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.BookVO;
import com.example.booktrading.entity.vo.UserVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.BookMapper;
import com.example.booktrading.mapper.OrderMapper;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.AdminService;
import com.example.booktrading.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员服务实现
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    
    private final UserMapper userMapper;
    private final BookMapper bookMapper;
    private final OrderMapper orderMapper;
    
    @Override
    public IPage<UserVO> getUserList(Integer page, Integer size, String keyword, Integer status) {
        checkAdminRole();
        
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getPhone, keyword));
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);
        
        IPage<User> userPage = userMapper.selectPage(pageParam, wrapper);
        return userPage.convert(this::convertToUserVO);
    }
    
    @Override
    public void updateUserStatus(Long userId, Integer status) {
        checkAdminRole();
        
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 不能禁用管理员
        if (user.getRole() == 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "不能修改管理员状态");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
    }
    
    @Override
    public Map<String, Object> getStatistics() {
        checkAdminRole();
        
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getRole, 0));
        stats.put("userCount", userCount);
        
        // 书籍统计
        Long bookCount = bookMapper.selectCount(null);
        stats.put("bookCount", bookCount);
        
        // 订单统计
        Long orderCount = orderMapper.selectCount(null);
        Long completedOrderCount = orderMapper.selectCount(new LambdaQueryWrapper<Orders>().eq(Orders::getStatus, 4));
        stats.put("orderCount", orderCount);
        stats.put("completedOrderCount", completedOrderCount);
        
        return stats;
    }

    
    @Override
    public IPage<BookVO> getBookList(Integer page, Integer size, String keyword, Integer status) {
        checkAdminRole();
        
        Page<Book> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword));
        }
        if (status != null) {
            wrapper.eq(Book::getStatus, status);
        }
        wrapper.orderByDesc(Book::getCreateTime);
        
        IPage<Book> bookPage = bookMapper.selectPage(pageParam, wrapper);
        return bookPage.convert(this::convertToBookVO);
    }
    
    @Override
    public void deleteBook(Long bookId) {
        checkAdminRole();
        
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        
        bookMapper.deleteById(bookId);
    }
    
    /**
     * 检查当前用户是否为管理员
     */
    private void checkAdminRole() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null || user.getRole() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
        }
    }
    
    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
    
    private BookVO convertToBookVO(Book book) {
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        
        // 获取卖家信息
        User seller = userMapper.selectById(book.getUserId());
        if (seller != null) {
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
        }
        
        return vo;
    }
}
