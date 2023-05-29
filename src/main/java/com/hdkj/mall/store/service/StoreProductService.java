package com.hdkj.mall.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.front.request.ProductRequest;
import com.hdkj.mall.front.request.IndexStoreProductSearchRequest;
import com.hdkj.mall.store.model.StoreProduct;
import com.hdkj.mall.store.request.StoreProductRequest;
import com.hdkj.mall.store.request.StoreProductSearchRequest;
import com.hdkj.mall.store.request.StoreProductStockRequest;
import com.hdkj.mall.store.response.StoreProductResponse;
import com.hdkj.mall.store.response.StoreProductTabsHeader;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * StoreProductService 接口
 */
public interface StoreProductService extends IService<StoreProduct> {

    /**
     * 获取产品列表Admin
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<StoreProductResponse> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * H5端使用
     * @param request
     * @param pageParamRequest
     * @param productIdList
     * @return
     */
    List<StoreProduct> getList(StoreProductSearchRequest request, PageParamRequest pageParamRequest,
                               List<Integer> productIdList);

    /**
     * 获取产品列表H5
     * @param request
     * @param pageParamRequest
     * @return
     */
    List<StoreProduct> getList(IndexStoreProductSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据id集合获取商品信息
     * @param productIds id集合
     * @return 商品信息
     */
    List<StoreProduct> getListInIds(List<Integer> productIds);
    /**
     * 基本参数获取
     * @param storeProduct 产品参数
     * @return 商品结果
     */
    StoreProduct getByEntity(StoreProduct storeProduct);

    /**
     * 保存产品信息
     * @param storeProductRequest
     * @return
     */
    boolean save(StoreProductRequest storeProductRequest);

    /**
     * 更新产品信息
     * @param storeProductRequest
     * @return
     */
    boolean update(StoreProductRequest storeProductRequest);

    /**
     * 产品详情
     * @param id
     * @return
     */
    StoreProductResponse getByProductId(int id);

    /**
     * 获取tabsHeader对应数量
     * @return
     */
    List<StoreProductTabsHeader> getTabsHeader( StoreProductSearchRequest storeProductSearchRequest);
    /**
     * 获取tabsHeader对应数量
     * @return
     */
    List<StoreProductTabsHeader> getTabsFwHeader( StoreProductSearchRequest storeProductSearchRequest,Integer type);

    /**
     * 添加库存
     * @param request
     * @return
     */
    boolean stockAddRedis(StoreProductStockRequest request);

    /**
     * 根据其他平台url导入产品信息
     * @param url 待倒入平台的url
     * @param tag 待导入平台标识
     * @return 待导入的商品信息
     */
    StoreProductRequest importProductFromUrl(String url, int tag) throws IOException, JSONException;

    /**
     * 获取推荐商品
     * @param limit 最大数据量
     * @return 推荐商品集合
     */
    List<StoreProduct> getRecommendStoreProduct(Integer limit);

    /**
     * 扣减库存加销量
     * @param productId 产品id
     * @param num 商品数量
     * @param type 是否限购 0=不限购
     * @return 扣减结果
     */
    boolean decProductStock(Integer productId, Integer num, Integer attrValueId, Integer type);

    List<Integer> getSecondaryCategoryByProductId(String productId);

    /**
     * 删除商品
     * @param productId 商品id
     * @param type 类型：recycle——回收站 delete——彻底删除
     * @return 删除结果
     */
    boolean deleteProduct(Integer productId, String type);

    /**
     * 恢复已删除商品
     * @param productId 商品id
     * @return 恢复结果
     */
    boolean reStoreProduct(Integer productId);

    /**
     * 后台任务批量操作库存
     */
    void consumeProductStock();

    /**
     * 扣减库存任务操作
     * @param storeProductStockRequest 扣减库存参数
     * @return 执行结果
     */
    boolean doProductStock(StoreProductStockRequest storeProductStockRequest);

    /**
     * 获取复制商品配置
     */
    MyRecord copyConfig();

    /**
     * 复制平台商品
     * @param url 商品链接
     * @return
     */
    MyRecord copyProduct(String url);

    /**
     * 添加/扣减库存
     * @param id 商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationStock(Integer id, Integer num, String type);

    /**
     * 下架
     * @param id 商品id
     */
    Boolean offShelf(Integer id);

    /**
     * 上架
     * @param id 商品id
     * @return Boolean
     */
    Boolean putOnShelf(Integer id);

    /**
     * 首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return CommonPage
     */
    List<StoreProduct> getIndexProduct(Integer type, PageParamRequest pageParamRequest);

    /**
     * 获取商品移动端列表
     * @param request 筛选参数
     * @param pageRequest 分页参数
     * @return List
     */
    List<StoreProduct> findH5List(ProductRequest request, PageParamRequest pageRequest);

    /**
     * 获取移动端商品详情
     * @param id 商品id
     * @return StoreProduct
     */
    StoreProduct getH5Detail(Integer id);

    /**
     * 获取购物车商品信息
     * @param productId 商品编号
     * @return StoreProduct
     */
    StoreProduct getCartByProId(Integer productId);

    /**
     * 根据商品ids获取对应的列表
     * @param productIdList 商品id列表
     * @return List<StoreProduct>
     */
    List<StoreProduct> findH5ListByProIds(List<Integer> productIdList);
}
