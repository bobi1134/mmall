package cn.mrx.mmall.common;

import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

/**
 * Author：Mr.X
 * Date：2017/10/23 20:10
 * Description：
 */
public class Const {

    public static final String CURRENT_USER = "current_user";

    public interface Role {
        int ROLE_CUSTOMER = 0;  //普通用户
        int ROLE_ADMIN = 1;     //管理员
    }

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";

    @Getter
    @AllArgsConstructor
    public enum ProductStatusEnum {
        ON_SALE(1, "在线");
        private int code;
        private String value;
    }

    public interface ProductListOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
}
