package com.hdkj.mall.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.store.model.StoreProductCate;
import com.hdkj.mall.store.request.StoreProductCateSearchRequest;

import java.util.List;

/**
 * StoreProductCateService 接口
 */
public interface StoreProductCateService extends IService<StoreProductCate> {

    List<StoreProductCate> getList(StoreProductCateSearchRequest request, PageParamRequest pageParamRequest);

    List<StoreProductCate> getByProductId(Integer productId);

}
