package cn.mrx.mmall.controller.backend;

import cn.mrx.mmall.common.Const;
import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.pojo.User;
import cn.mrx.mmall.service.ICategoryService;
import cn.mrx.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Author：Mr.X
 * Date：2017/11/12 21:19
 * Description：
 */
@RestController
@RequestMapping("mgr/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    // 添加分类
    @PostMapping("add_category")
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iCategoryService.addCategory(categoryName, parentId);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 更新分类名称
    @PostMapping("update_category_name")
    public ServerResponse updateCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iCategoryService.updateCategoryName(categoryId, categoryName);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 根据categoryId获取对应的子分类
    @GetMapping("get_children_category")
    public ServerResponse getChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iCategoryService.getChildrenCategory(categoryId);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 根据categoryId查询自己的id以及子分类的id集合
    @GetMapping("get_id")
    public ServerResponse getSelfAndChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iCategoryService.getSelfAndChildrenCategory(categoryId);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }
}
