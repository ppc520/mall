package com.hdkj.mall.store.service;


/**
 * 订单任务服务 StoreOrderService 接口
 */
 public interface OrderTaskService{

     void cancelByUser();

     void refundApply();

     void refundApply_fw();

     void complete();

    void orderPaySuccessAfter();

    /**
     * 自动取消未支付订单
     */
    void autoCancel();

    /**
     * 订单收货
     */
    void orderReceiving();

    /**
     * 订单自动完成
     */
    void autoComplete();


    /**
     * 自动派送订单
     */
    void autoSendOrder();

    /**
     * 服务订单7天自动评价
     */
    void autoSendOrderReply();

}
