package com.wx.orm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wx.orm.entity.GoodsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GoodsMapper extends BaseMapper<GoodsDO> {
    @Select("select distinct category from goods where")
    List<String> queryProductCategory();

    @Select("select distinct brand from goods")
    List<String> queryProductBrand();
}
