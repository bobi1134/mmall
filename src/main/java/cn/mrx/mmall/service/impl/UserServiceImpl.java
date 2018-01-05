package cn.mrx.mmall.service.impl;

import cn.mrx.mmall.common.Const;
import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.common.TokenCache;
import cn.mrx.mmall.dao.UserMapper;
import cn.mrx.mmall.pojo.User;
import cn.mrx.mmall.service.IUserService;
import cn.mrx.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Author：Mr.X
 * Date：2017/10/23 15:41
 * Description：
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        // 1、检查用户名是否存在
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) return ServerResponse.error("用户名不存在");
        // 2、检查用户名和密码是否正确
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) return ServerResponse.error("密码错误");
        // 3、用户名密码正确后，将密码置空
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.success("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {
        // 1、检查用户名是否存在
        int resultCount = userMapper.checkUsername(user.getUsername());
        if (resultCount > 0) return ServerResponse.error("用户名已存在");
        // 2、检查邮箱是否存在
        resultCount = userMapper.checkEmail(user.getEmail());
        if (resultCount > 0) return ServerResponse.error("邮箱已存在");
        // 3、设置角色,默认为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);
        // 4、MD5加密密码
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        // 5、插入数据库
        resultCount = userMapper.insert(user);
        if (resultCount == 0) return ServerResponse.error("注册失败");
        System.out.println("自动生成的主键-->" + user.getId());
        return ServerResponse.error("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNoneBlank(type)) {
            if (Const.USERNAME.equals(type)) {
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) return ServerResponse.error("用户名已存在");
            }

            if (Const.EMAIL.equals(type)) {
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) return ServerResponse.error("邮箱已存在");
            }
        } else {
            return ServerResponse.error("参数错误");
        }
        return ServerResponse.success("校验成功");
    }

    @Override
    public ServerResponse<String> getQuestion(String username) {
        ServerResponse response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()) return ServerResponse.error("用户不存在");
        String question = userMapper.selectQuestionByUsername(username);
        if (StringUtils.isNotBlank(question)) return ServerResponse.success(question);
        return ServerResponse.error("找回密码的问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            // 用户将问题和答案验证正确后,生成token放在缓存中
            String forgetToken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.success(forgetToken);
        }
        return ServerResponse.error("问题的答案错误");
    }

    @Override
    public ServerResponse<String> restPassword(String username, String newPassword, String forgetToken) {
        if (StringUtils.isBlank(forgetToken)) return ServerResponse.error("参数错误,forgetToken需要传递");
        ServerResponse response = this.checkValid(username, Const.USERNAME);
        if (response.isSuccess()) return ServerResponse.error("用户不存在");
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(token)) return ServerResponse.error("token无效或者过期");
        if (StringUtils.equals(forgetToken, token)) {
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int resultCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (resultCount > 0) return ServerResponse.success("重置密码成功");
        } else {
            return ServerResponse.error("token错误,请重新获取重置密码的token");
        }
        return ServerResponse.error("修改密码失败");
    }

    @Override
    public ServerResponse<String> restPassword(User user, String oldPassword, String newPassword) {
        // 防止横向越权,即保证必须是当前用户修改自己的密码
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword), user.getId());
        if (resultCount == 0) return ServerResponse.error("旧密码错误");
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount > 0) return ServerResponse.success("密码更新成功");
        return ServerResponse.error("密码更新失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {
        User user = userMapper.selectByPrimaryKey(id);
        if (user == null) return ServerResponse.error("找不到当前用户");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.success(user);
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {
        // 1、校验邮箱,其他用户中没使用过的
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) return ServerResponse.error("邮箱已存在,请更换email再尝试更新");
        // 2、只更新以下数据
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());
        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if (updateCount > 0) return ServerResponse.success("更新个人信息成功", updateUser);
        return ServerResponse.success("更新个人信息失败");
    }

    @Override
    public ServerResponse checkAdminRole(User user) {
        if (user != null && user.getRole() == Const.Role.ROLE_ADMIN) return ServerResponse.success();
        return ServerResponse.error();
    }
}
