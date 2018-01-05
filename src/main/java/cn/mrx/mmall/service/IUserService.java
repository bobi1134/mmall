package cn.mrx.mmall.service;

import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.pojo.User;

/**
 * Author：Mr.X
 * Date：2017/10/23 15:41
 * Description：
 */
public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> getQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> restPassword(String username, String newPassword, String forgetToken);

    ServerResponse<String> restPassword(User user, String oldPassword, String newPassword);

    ServerResponse<User> getInformation(Integer id);

    ServerResponse<User> updateInformation(User user);

    ServerResponse checkAdminRole(User user);
}
