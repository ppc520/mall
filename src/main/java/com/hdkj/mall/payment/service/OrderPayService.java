package com.hdkj.mall.payment.service;

import com.hdkj.mall.front.request.OrderPayRequest;
import com.hdkj.mall.front.response.OrderPayResultResponse;
import com.hdkj.mall.store.model.StoreOrder;

/**
 * 订单支付
 */
public interface OrderPayService{

    /**
     * 支付成功处理
     * @param storeOrder 订单
     */
    Boolean paySuccess(StoreOrder storeOrder);

    /**
     * 余额支付
     * @param storeOrder 订单
     * @return Boolean
     */
    Boolean yuePay(StoreOrder storeOrder);

    /**
     * 订单支付
     * @param orderPayRequest 支付参数
     * @param ip    ip
     * @return OrderPayResultResponse
     */
    OrderPayResultResponse payment(OrderPayRequest orderPayRequest, String ip);

    /**
     * 订单支付
     * @param orderPayRequest 支付参数
     * @param ip    ip
     * @return OrderPayResultResponse
     */
    OrderPayResultResponse payment1(OrderPayRequest orderPayRequest, String ip);
}
