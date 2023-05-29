package com.hdkj.mall.store.service;


import com.hdkj.mall.user.model.UserLjhsOrder;
import com.hdkj.mall.store.model.StoreOrder;

/**
 * 订单任务服务
 */
public interface StoreOrderTaskService {

    Boolean cancelByUser(StoreOrder storeOrder);

    Boolean complete(StoreOrder storeOrder);

    Boolean refundOrder(StoreOrder storeOrder);

    Boolean refundOrder_fw(UserLjhsOrder userLjhsOrder);

    Boolean autoCancel(StoreOrder storeOrder);

    Boolean orderReceiving(Integer orderId);
}
