package cn.mrx.mmall.service.impl;

import cn.mrx.mmall.common.ResponseCode;
import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.dao.CategoryMapper;
import cn.mrx.mmall.pojo.Category;
import cn.mrx.mmall.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Author：Mr.X
 * Date：2017/11/12 21:29
 * Description：
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true); //这个分类是可用的
        int rowCount = categoryMapper.insert(category);
        if (rowCount > 0) return ServerResponse.success("添加分类成功");
        return ServerResponse.error("添加分类失败");
    }

    @Override
    public ServerResponse updateCategoryName(Integer categoryId, String categoryName) {
        if (categoryId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount > 0) return ServerResponse.success("更新分类名字成功");
        return ServerResponse.error("更新分类名字失败");
    }

    @Override
    public ServerResponse getChildrenCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.getChildrenCategory(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) logger.info("未找到当前分类的子分类");
        return ServerResponse.success(categoryList);
    }

    @Override
    public ServerResponse getSelfAndChildrenCategory(Integer categoryId) {
        if (categoryId != null) return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数错误");
        Set<Category> categorySet = Sets.newHashSet(); // 初始化Set
        findChildCategory(categorySet, categoryId); // 递归
        List<Integer> categoryIdList = Lists.newArrayList(); // 初始化List
        for (Category categoryItem : categorySet) {
            categoryIdList.add(categoryItem.getId());
        }
        return ServerResponse.success(categoryIdList);
    }

    //递归算法,算出子节点
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        // 将自己添加进去
        if (category != null) categorySet.add(category);
        //查找子节点,递归算法一定要有一个退出的条件
        List<Category> categoryList = categoryMapper.getChildrenCategory(categoryId);
        for (Category categoryItem : categoryList) {
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
