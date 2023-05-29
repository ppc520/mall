package com.hdkj.mall.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.store.model.StoreOrder;
import com.hdkj.mall.store.request.StoreOrderRefundRequest;


/**
 * StoreOrderRefundService 接口
 */
public interface StoreOrderRefundService extends IService<StoreOrder> {

    void refund(StoreOrderRefundRequest request, StoreOrder storeOrder);
}
