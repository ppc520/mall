package com.hdkj.mall.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.store.request.StoreProductAttrResultSearchRequest;
import com.hdkj.mall.store.model.StoreProductAttrResult;

import java.util.List;

/**
 * StoreProductAttrResultService 接口
 */
public interface StoreProductAttrResultService extends IService<StoreProductAttrResult> {

    List<StoreProductAttrResult> getList(StoreProductAttrResultSearchRequest request, PageParamRequest pageParamRequest);

    StoreProductAttrResult getByProductId(int productId);

    void deleteByProductId(int productId, int type);

    /**
     * 根据商品属性值集合查询
     * @param storeProductAttrResult 查询参数
     * @return  查询结果
     */
    List<StoreProductAttrResult> getByEntity(StoreProductAttrResult storeProductAttrResult);
}
