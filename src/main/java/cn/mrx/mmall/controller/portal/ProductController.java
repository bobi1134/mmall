package cn.mrx.mmall.controller.portal;

import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Author：Mr.X
 * Date：2017/11/21 10:54
 * Description：
 */
@RestController
@RequestMapping("product")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    // 普通用户-获取商品详情
    @GetMapping("get_product_detail")
    public ServerResponse getProductDetail(Integer productId) {
        return iProductService.getProductDetail(productId);
    }

    // 商品列表,包括关键字搜索
    @PostMapping("list")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "categoryId", required = false) Integer categoryId,
                               @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                               @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return iProductService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }
}
