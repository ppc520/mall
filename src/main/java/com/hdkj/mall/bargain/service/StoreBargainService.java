package com.hdkj.mall.bargain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.bargain.model.StoreBargain;
import com.hdkj.mall.bargain.request.StoreBargainSearchRequest;
import com.hdkj.mall.bargain.request.StoreBargainRequest;
import com.hdkj.mall.bargain.response.StoreBargainResponse;
import com.hdkj.mall.front.request.BargainFrontRequest;
import com.hdkj.mall.front.response.BargainDetailH5Response;
import com.hdkj.mall.front.response.BargainHeaderResponse;
import com.hdkj.mall.front.response.BargainIndexResponse;
import com.hdkj.mall.front.response.StoreBargainDetailResponse;
import com.hdkj.mall.store.request.StoreProductStockRequest;
import com.hdkj.mall.store.response.StoreProductResponse;

import java.util.HashMap;
import java.util.List;

/**
 * 砍价 Service
 */
public interface StoreBargainService extends IService<StoreBargain> {

    /**
     * 分页显示砍价商品列表
     */
    PageInfo<StoreBargainResponse> getList(StoreBargainSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增砍价商品
     */
    boolean saveBargain(StoreBargainRequest storeBargainRequest);

    /**
     * 删除砍价商品
     */
    boolean deleteById(Integer id);

    /**
     * 修改砍价商品
     */
    boolean updateBargain(StoreBargainRequest storeBargainRequest);

    /**
     * 修改砍价商品状态
     */
    boolean updateBargainStatus(Integer id, boolean status);

    /**
     * 查询砍价商品详情
     */
    StoreProductResponse getAdminDetail(Integer id);

    /**
     * H5 砍价商品列表
     * @return PageInfo<StoreBargainDetailResponse>
     */
    PageInfo<StoreBargainDetailResponse> getH5List(PageParamRequest pageParamRequest);

    /**
     * H5 获取砍价商品详情信息
     * @return BargainDetailResponse
     */
    BargainDetailH5Response getH5Detail(Integer id);

    /**
     * 获取当前时间的砍价商品
     * @return List<StoreBargain>
     */
    List<StoreBargain> getCurrentBargainByProductId(Integer productId);

    /**
     * 创建砍价活动
     * @return MyRecord
     */
    MyRecord start(BargainFrontRequest bargainFrontRequest);

    /**
     * 砍价商品根据实体查询
     * @return Boolean
     */
    List<StoreBargain> getByEntity(StoreBargain storeBargainParam);

    /**
     * 添加库存
     */
    Boolean stockAddRedis(StoreProductStockRequest stockRequest);

    /**
     * 后台任务批量操作库存
     */
    void consumeProductStock();

    /**
     * 砍价活动结束后处理
     */
    void stopAfterChange();

    /**
     * 商品是否存在砍价活动
     * @param productId 商品编号
     */
    Boolean isExistActivity(Integer productId);

    /**
     * 查询带异常
     * @param id 砍价商品id
     * @return StoreBargain
     */
    StoreBargain getByIdException(Integer id);

    /**
     * 添加/扣减库存
     * @param id 秒杀商品id
     * @param num 数量
     * @param type 类型：add—添加，sub—扣减
     */
    Boolean operationStock(Integer id, Integer num, String type);

    /**
     * 砍价首页信息
     * @return BargainIndexResponse
     */
    BargainIndexResponse getIndexInfo();

    /**
     * 获取砍价列表header
     * @return BargainHeaderResponse
     */
    BargainHeaderResponse getHeader();

    /**
     * 根据id数组获取砍价商品map
     * @param bargainIdList 砍价商品id数组
     * @return HashMap<Integer, StoreBargain>
     */
    HashMap<Integer, StoreBargain> getMapInId(List<Integer> bargainIdList);
}
