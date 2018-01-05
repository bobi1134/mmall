package cn.mrx.mmall.dao;

import cn.mrx.mmall.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    /** My ######################################################### */
    List<Category> getChildrenCategory(Integer categoryId);
}