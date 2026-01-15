package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.booktrading.constant.BookStatus;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.po.Book;
import com.example.booktrading.entity.po.Category;
import com.example.booktrading.entity.po.Favorite;
import com.example.booktrading.entity.po.User;
import com.example.booktrading.entity.vo.BookVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.BookMapper;
import com.example.booktrading.mapper.CategoryMapper;
import com.example.booktrading.mapper.FavoriteMapper;
import com.example.booktrading.mapper.UserMapper;
import com.example.booktrading.service.FavoriteService;
import com.example.booktrading.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    
    private final FavoriteMapper favoriteMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    
    @Override
    public void add(Long bookId) {
        Long userId = UserContext.getUserId();
        
        // 检查书籍是否存在
        Book book = bookMapper.selectById(bookId);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        
        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBookId, bookId);
        if (favoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(ResultCode.FAVORITE_EXISTS);
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favoriteMapper.insert(favorite);
    }
    
    @Override
    public void remove(Long bookId) {
        Long userId = UserContext.getUserId();
        
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBookId, bookId);
        favoriteMapper.delete(wrapper);
    }
    
    @Override
    public IPage<BookVO> list(Integer page, Integer size) {
        Long userId = UserContext.getUserId();
        
        // 查询收藏记录
        Page<Favorite> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).orderByDesc(Favorite::getCreateTime);
        
        IPage<Favorite> favoritePage = favoriteMapper.selectPage(pageParam, wrapper);
        
        // 获取书籍ID列表
        List<Long> bookIds = favoritePage.getRecords().stream()
                .map(Favorite::getBookId)
                .collect(Collectors.toList());
        
        if (bookIds.isEmpty()) {
            return new Page<BookVO>(page, size).setRecords(List.of());
        }
        
        // 查询书籍信息
        List<Book> books = bookMapper.selectBatchIds(bookIds);
        
        // 转换为VO
        List<BookVO> bookVOs = books.stream().map(this::convertToVO).collect(Collectors.toList());
        
        Page<BookVO> result = new Page<>(page, size);
        result.setRecords(bookVOs);
        result.setTotal(favoritePage.getTotal());
        return result;
    }
    
    @Override
    public boolean check(Long bookId) {
        Long userId = UserContext.getUserId();
        
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBookId, bookId);
        return favoriteMapper.selectCount(wrapper) > 0;
    }
    
    private BookVO convertToVO(Book book) {
        BookVO vo = new BookVO();
        BeanUtils.copyProperties(book, vo);
        
        BookStatus status = BookStatus.fromCode(book.getStatus());
        vo.setStatusDesc(status != null ? status.getDesc() : "未知");
        
        Category category = categoryMapper.selectById(book.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        User seller = userMapper.selectById(book.getUserId());
        if (seller != null) {
            vo.setSellerId(seller.getId());
            vo.setSellerName(seller.getUsername());
            vo.setSellerAvatar(seller.getAvatar());
            vo.setSellerRating(seller.getAvgRating());
        }
        
        return vo;
    }
}
