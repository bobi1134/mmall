package cn.mrx.mmall.controller.portal;

import cn.mrx.mmall.common.Const;
import cn.mrx.mmall.common.ResponseCode;
import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.pojo.User;
import cn.mrx.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Author：Mr.X
 * Date：2017/10/23 14:39
 * Description：
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private IUserService iUserService;

    // 登录
    @PostMapping("login")
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> serverResponse = iUserService.login(username, password);
        if (serverResponse.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, serverResponse.getData());
        }
        return serverResponse;
    }

    // 登出
    @GetMapping("logout")
    public ServerResponse<User> logout(HttpSession session) {
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.success("退出成功");
    }

    // 注册
    @PostMapping("register")
    public ServerResponse<String> register(User user) {
        return iUserService.register(user);
    }

    // 用户名或邮箱校验,username代表用户名,email代表邮箱
    @PostMapping("check_valid")
    public ServerResponse<String> checkValid(String str, String type) {
        return iUserService.checkValid(str, type);
    }

    // 获取当前登录用户信息
    @GetMapping("get_user_info")
    public ServerResponse<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user != null) return ServerResponse.success(user);
        return ServerResponse.error("用户未登录，无法获取当前用户信息");
    }

    // 忘记密码时,根据用户名获取密保问题
    @PostMapping("f_get_question")
    public ServerResponse<String> getQuestion(String username) {
        return iUserService.getQuestion(username);
    }

    // 忘记密码时,根据用户名+密保问题+密保答案校验是否正确,校验正确则返回一个token
    @PostMapping("f_check_answer")
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        return iUserService.checkAnswer(username, question, answer);
    }

    // 忘记密码时,根据新密码+token重置密码
    @PostMapping("f_rest_password")
    public ServerResponse<String> restPassword(String username, String newPassword, String forgetToken) {
        return iUserService.restPassword(username, newPassword, forgetToken);
    }

    // 登录状态时重置密码
    @PostMapping("rest_password")
    public ServerResponse<String> restPassword(HttpSession session, String oldPassword, String newPassword) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        return iUserService.restPassword(user, oldPassword, newPassword);
    }

    // 更新用户时,获取用户信息
    @GetMapping("get_information")
    public ServerResponse<User> getInformation(HttpSession session) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) return ServerResponse.error(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录status=10");
        return iUserService.getInformation(currentUser.getId());
    }

    // 更新用户信息
    @PostMapping("update_information")
    public ServerResponse<User> updateInformation(HttpSession session, User user) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) return ServerResponse.error("用户未登录");
        user.setId(currentUser.getId()); // 防止越权
        user.setUsername(currentUser.getUsername()); // 用户名禁止修改
        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess()) session.setAttribute(Const.CURRENT_USER, response.getData());
        return response;
    }
}
