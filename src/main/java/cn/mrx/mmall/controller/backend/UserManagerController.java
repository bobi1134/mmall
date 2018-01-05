package cn.mrx.mmall.controller.backend;

import cn.mrx.mmall.common.Const;
import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.pojo.User;
import cn.mrx.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Author：Mr.X
 * Date：2017/11/2 14:01
 * Description：
 */
@RestController
@RequestMapping("mgr/user")
public class UserManagerController {

    @Autowired
    private IUserService iUserService;

    // 管理员登录
    @PostMapping("login")
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) session.setAttribute(Const.CURRENT_USER, user);
            else return ServerResponse.error("不是管理员,无法登录");
        }
        return response;
    }
}
