package com.example.booktrading.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.booktrading.entity.po.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏Mapper
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
    
}
