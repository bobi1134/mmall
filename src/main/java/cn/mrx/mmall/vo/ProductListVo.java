package cn.mrx.mmall.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Author：Mr.X
 * Date：2017/11/20 21:03
 * Description：
 */
@Data
public class ProductListVo {
    private Integer id;
    private Integer categoryId;
    private String name;
    private String subtitle;
    private String mainImage;
    private BigDecimal price;
    private Integer status;
    private String imageHost;
}
