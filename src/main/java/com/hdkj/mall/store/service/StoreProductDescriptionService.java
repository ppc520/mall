package com.hdkj.mall.store.service;

import com.common.PageParamRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.store.model.StoreProductDescription;
import com.hdkj.mall.store.request.StoreProductDescriptionSearchRequest;

import java.util.List;

/**
 * StoreProductDescriptionService 接口
 */
public interface StoreProductDescriptionService extends IService<StoreProductDescription> {

    List<StoreProductDescription> getList(StoreProductDescriptionSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据商品id和type删除对应描述
     * @param productId 商品id
     * @param type      类型
     */
    void deleteByProductId(int productId,int type);
}
