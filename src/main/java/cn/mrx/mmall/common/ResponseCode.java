package cn.mrx.mmall.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author：Mr.X
 * Date：2017/10/23 17:49
 * Description：
 */
@Getter
@AllArgsConstructor
public enum ResponseCode {

    SUCCESS(0, "成功"),
    ERROR(1, "失败"),
    NEED_LOGIN(10, "需要登录"),
    ILLEGAL_ARGUMENT(2, "参数错误");

    private final int code;
    private final String msg;
}
