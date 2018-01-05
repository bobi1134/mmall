package cn.mrx.mmall.service.impl;

import cn.mrx.mmall.common.Const;
import cn.mrx.mmall.common.ResponseCode;
import cn.mrx.mmall.common.ServerResponse;
import cn.mrx.mmall.dao.CategoryMapper;
import cn.mrx.mmall.dao.ProductMapper;
import cn.mrx.mmall.pojo.Category;
import cn.mrx.mmall.pojo.Product;
import cn.mrx.mmall.service.ICategoryService;
import cn.mrx.mmall.service.IProductService;
import cn.mrx.mmall.util.DateTimeUtil;
import cn.mrx.mmall.util.FTPUtil;
import cn.mrx.mmall.util.PropertiesUtil;
import cn.mrx.mmall.vo.ProductDetailVo;
import cn.mrx.mmall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Author：Mr.X
 * Date：2017/11/20 15:14
 * Description：
 */
@Service
public class ProductServiceImpl implements IProductService {

    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            // 默认取子图的第一张图片作为主图
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) product.setMainImage(subImageArray[0]);
            }

            if (product.getId() != null) {
                // 更新
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if (rowCount > 0) return ServerResponse.success("更新产品成功");
                return ServerResponse.error("更新产品失败");
            } else {
                // 添加
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) return ServerResponse.success("新增产品成功");
                return ServerResponse.error("新增产品失败");
            }
        }
        return ServerResponse.error("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse updateSaleStatus(Integer productId, Integer status) {
        // 参数判断
        if (productId == null || status == null)
            return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        // 修改逻辑
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) return ServerResponse.success("修改产品销售状态成功");
        return ServerResponse.error("修改产品销售状态失败");
    }

    @Override
    public ServerResponse manageProductDetail(Integer productId) {
        // 参数判断
        if (productId == null)
            return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        // 查询逻辑
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) return ServerResponse.error("产品已下架或者删除");
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.success(productDetailVo);
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        // 图片主机地址
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.mmall.com/"));
        // 父分类,如果为空则默认根节点
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) productDetailVo.setParentCategoryId(0);
        else productDetailVo.setParentCategoryId(category.getParentId());
        // 创建时间、更新时间
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    @Override
    public ServerResponse getProductList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.success(pageResult);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.mmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }

    @Override
    public ServerResponse productSearch(String productName, Integer productId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName))
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList) {
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.success(pageResult);
    }

    @Override
    public String upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;
        logger.info("开始上传文件,上传文件的文件名:{},上传的路径:{},新文件名:{}", fileName, path, uploadFileName);
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);
        try {
            // 上传到tomcat
            file.transferTo(targetFile);
            // 上传到FTP
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            // 删除tomcat文件
            targetFile.delete();
        } catch (IOException e) {
            logger.error("上传文件异常", e);
            return null;
        }
        return targetFile.getName();
    }

    // portal
    @Override
    public ServerResponse getProductDetail(Integer productId) {
        if (productId == null)
            return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) return ServerResponse.error("产品已下架或者删除");
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) return ServerResponse.error("产品已下架或者删除");
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.success(productDetailVo);
    }

    @Override
    public ServerResponse getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize, String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null)
            return ServerResponse.error(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getMsg());
        List<Integer> categoryIdList = new ArrayList<>();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                // 没有该分类,并且还没有关键字,这个时候返回一个空的结果集,不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.success(pageInfo);
            }
            categoryIdList = iCategoryService.getSelfAndChildrenCategory(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword))
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword) ? null : keyword, categoryIdList.size() == 0 ? null : categoryIdList);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.success(pageInfo);
    }
}
