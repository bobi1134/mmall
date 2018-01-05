package cn.mrx.mmall.controller.backend;

import cn.mrx.mmall.common.Const;
import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.pojo.Product;
import cn.mrx.mmall.pojo.User;
import cn.mrx.mmall.service.IProductService;
import cn.mrx.mmall.service.IUserService;
import cn.mrx.mmall.util.PropertiesUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Author：Mr.X
 * Date：2017/11/20 15:13
 * Description：
 */
@RestController
@RequestMapping("mgr/product")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IUserService iUserService;

    // 更新或保存商品
    @PostMapping("product_save")
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iProductService.saveOrUpdateProduct(product);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 更新商品状态
    @PostMapping("update_sale_status")
    public ServerResponse updateSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iProductService.updateSaleStatus(productId, status);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 管理员-获取商品详情
    @GetMapping("get_product_detail")
    public ServerResponse getProductDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iProductService.manageProductDetail(productId);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 分页获取商品列表信息
    @PostMapping("get_product_list")
    public ServerResponse getProductList(HttpSession session,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iProductService.getProductList(pageNum, pageSize);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 分页搜索商品,根据商品名和商品id搜索
    @PostMapping("product_search")
    public ServerResponse productSearch(HttpSession session,
                                        String productName,
                                        Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess())
            return iProductService.productSearch(productName, productId, pageNum, pageSize);
        return ServerResponse.error("无权限操作,需要管理员权限");
    }

    // 上传文件到ftp服务器
    @PostMapping("upload")
    public ServerResponse upload(HttpSession session,
                                 HttpServletRequest request,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile file) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) return ServerResponse.error("用户未登录");
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iProductService.upload(file, path);
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;
            fileMap.put("url", url);
            return ServerResponse.success("上传成功", fileMap);
        }
        return ServerResponse.error("无权限操作,需要管理员权限");
    }
}
