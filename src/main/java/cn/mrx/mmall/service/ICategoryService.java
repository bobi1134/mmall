package cn.mrx.mmall.service;

import cn.mrx.mmall.common.ServerResponse;

import java.util.List;

/**
 * Author：Mr.X
 * Date：2017/11/12 21:29
 * Description：
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse getChildrenCategory(Integer categoryId);

    ServerResponse<List<Integer>> getSelfAndChildrenCategory(Integer categoryId);
}
