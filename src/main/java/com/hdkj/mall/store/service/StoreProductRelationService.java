package com.hdkj.mall.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.front.request.UserCollectAllRequest;
import com.hdkj.mall.front.response.UserRelationResponse;
import com.hdkj.mall.store.model.StoreProductRelation;
import com.hdkj.mall.store.request.StoreProductRelationSearchRequest;
import com.hdkj.mall.store.model.StoreProduct;

import java.util.List;

/**
 * StoreProductRelationService 接口
 */
public interface StoreProductRelationService extends IService<StoreProductRelation> {

    List<StoreProduct> getList(StoreProductRelationSearchRequest request, PageParamRequest pageParamRequest);

    List<StoreProductRelation> getList(Integer productId, String type);

    /**
     * 取消收藏
     * @param requestJson 收藏idsJson
     * @return Boolean
     */
    Boolean delete(String requestJson);

    boolean all(UserCollectAllRequest request);

    List<StoreProductRelation> getLikeOrCollectByUser(Integer userId, Integer productId,boolean isLike);

    /**
     * 获取用户收藏列表
     * @param pageParamRequest 分页参数
     * @return List<UserRelationResponse>
     */
    List<UserRelationResponse> getUserList(PageParamRequest pageParamRequest);

    /**
     * 获取用户的收藏数量
     * @param uid 用户uid
     * @return 收藏数量
     */
    Integer getCollectCountByUid(Integer uid);

    /**
     * 根据商品Id取消收藏
     * @param proId 商品Id
     * @return Boolean
     */
    Boolean deleteByProId(Integer proId);
}
