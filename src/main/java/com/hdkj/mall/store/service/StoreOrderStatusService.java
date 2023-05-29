package com.hdkj.mall.store.service;

import com.common.PageParamRequest;
import com.hdkj.mall.store.request.StoreOrderStatusSearchRequest;
import com.hdkj.mall.store.model.StoreOrderStatus;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * StoreOrderStatusService 接口
 */
public interface StoreOrderStatusService extends IService<StoreOrderStatus> {

    List<StoreOrderStatus> getList(StoreOrderStatusSearchRequest request, PageParamRequest pageParamRequest);

    Boolean saveRefund(Integer orderId, BigDecimal amount, String message);

    Boolean createLog(Integer orderId, String type, String message);

    Boolean addLog(Integer orderId, String type, String message);

    /**
     * 根据实体参数获取
     * @param storeOrderStatus 订单状态参数
     * @return 订单状态结果
     */
    List<StoreOrderStatus> getByEntity(StoreOrderStatus storeOrderStatus);

    /**
     * 根据订单id获取最后一条记录
     * @param orderId 订单id
     * @return
     */
    StoreOrderStatus getLastByOrderId(Integer orderId);
}
