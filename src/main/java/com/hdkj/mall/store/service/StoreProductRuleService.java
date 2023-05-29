package com.hdkj.mall.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.store.request.StoreProductRuleRequest;
import com.hdkj.mall.store.request.StoreProductRuleSearchRequest;
import com.hdkj.mall.store.model.StoreProductRule;

import java.util.List;

/**
 * StoreProductRuleService 接口
 */
public interface StoreProductRuleService extends IService<StoreProductRule> {

    List<StoreProductRule> getList(StoreProductRuleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增商品规格
     * @param storeProductRuleRequest 规格参数
     * @return 新增结果
     */
    boolean save(StoreProductRuleRequest storeProductRuleRequest);
}
