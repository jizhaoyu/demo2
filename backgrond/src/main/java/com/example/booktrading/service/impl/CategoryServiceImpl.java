package com.example.booktrading.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.booktrading.constant.ResultCode;
import com.example.booktrading.entity.po.Category;
import com.example.booktrading.entity.vo.CategoryVO;
import com.example.booktrading.exception.BusinessException;
import com.example.booktrading.mapper.BookMapper;
import com.example.booktrading.mapper.CategoryMapper;
import com.example.booktrading.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;
    
    @Override
    public List<Category> list() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        return categoryMapper.selectList(wrapper);
    }
    
    @Override
    public List<CategoryVO> listWithBookCount() {
        List<Category> categories = list();
        return categories.stream().map(category -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(category, vo);
            vo.setBookCount(bookMapper.countByCategoryId(category.getId()));
            return vo;
        }).collect(Collectors.toList());
    }
    
    @Override
    public void create(Category category) {
        categoryMapper.insert(category);
    }
    
    @Override
    public void update(Long id, Category category) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        
        existing.setName(category.getName());
        existing.setSort(category.getSort());
        categoryMapper.updateById(existing);
    }
    
    @Override
    public void delete(Long id) {
        Category existing = categoryMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }
        
        // 检查是否有关联书籍
        int bookCount = bookMapper.countByCategoryId(id);
        if (bookCount > 0) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_BOOKS);
        }
        
        categoryMapper.deleteById(id);
    }
}
