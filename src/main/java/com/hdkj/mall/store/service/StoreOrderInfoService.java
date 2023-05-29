package com.hdkj.mall.store.service;

import com.common.PageParamRequest;
import com.hdkj.mall.store.vo.StoreOrderInfoOldVo;
import com.hdkj.mall.store.model.StoreOrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.store.request.StoreOrderInfoSearchRequest;
import com.hdkj.mall.store.vo.StoreOrderInfoVo;

import java.util.HashMap;
import java.util.List;

/**
 * StoreOrderInfoService 接口
 */
public interface StoreOrderInfoService extends IService<StoreOrderInfo> {

    List<StoreOrderInfo> getList(StoreOrderInfoSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<Integer, List<StoreOrderInfoOldVo>> getMapInId(List<Integer> orderIdList);

    List<StoreOrderInfoOldVo> getOrderListByOrderId(Integer orderId);

    /**
     * 批量添加订单详情
     * @param storeOrderInfos 订单详情集合
     * @return 保存结果
     */
    boolean saveOrderInfos(List<StoreOrderInfo> storeOrderInfos);

    /**
     * 通过订单编号和规格号查询
     * @param uni 规格号
     * @param orderId 订单编号
     * @return StoreOrderInfo
     */
    StoreOrderInfo getByUniAndOrderId(String uni, Integer orderId);

    /**
     * 获取订单详情vo列表
     * @param orderId 订单id
     * @return List<StoreOrderInfoVo>
     */
    List<StoreOrderInfoVo> getVoListByOrderId(Integer orderId);
}
