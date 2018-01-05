package cn.mrx.mmall.service;

import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.pojo.Product;
import cn.mrx.mmall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author：Mr.X
 * Date：2017/11/20 15:14
 * Description：
 */
public interface IProductService {

    ServerResponse saveOrUpdateProduct(Product product);

    ServerResponse updateSaleStatus(Integer productId, Integer status);

    ServerResponse manageProductDetail(Integer productId);

    ServerResponse getProductList(int pageNum, int pageSize);

    ServerResponse productSearch(String productName, Integer productId, int pageNum, int pageSize);

    String upload(MultipartFile file, String path);

    // portal
    ServerResponse getProductDetail(Integer productId);

    ServerResponse getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy);
}
