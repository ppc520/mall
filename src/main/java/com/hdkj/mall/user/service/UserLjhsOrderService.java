package com.hdkj.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.front.response.StoreProductReplayCountResponse;
import com.hdkj.mall.user.model.UserLjhsOrder;
import com.hdkj.mall.user.model.UserLjhsOrderReply;
import com.hdkj.mall.user.model.UserLjhsOrderStatus;
import com.hdkj.mall.user.request.UserLjhsOrdeRequest;
import com.hdkj.mall.user.request.UserOthersOrdeRequest;
import com.hdkj.mall.user.response.UserLjhsOrderNum;
import com.hdkj.mall.user.response.UserLjhsOrderReplyResponse;
import com.hdkj.mall.user.response.UserLjhsOrderResponse;
import com.hdkj.mall.user.response.UserOthersOrderResponse;

import java.util.List;

/**
 * UserLjhsOrderService 用户垃圾回收订单
 */
public interface UserLjhsOrderService extends IService<UserLjhsOrder> {

    Boolean createLjdrOder(UserLjhsOrdeRequest userLjhsOrdeRequest);

    Boolean createOthersOder(UserOthersOrdeRequest userOthersOrdeRequest);

    Boolean updateLjdrOder(UserLjhsOrdeRequest userLjhsOrdeRequest);

    Boolean sericeOrderComment(UserLjhsOrderReply userLjhsOrderReply);

    /**
     * 订单列表
     */
    CommonPage<UserLjhsOrderResponse> listLjdrOrder(UserLjhsOrdeRequest request, PageParamRequest pageRequest);

    /**
     * 订单列表
     */
    CommonPage<UserOthersOrderResponse> listOthersOrder(UserOthersOrdeRequest request, PageParamRequest pageRequest);
    /**
     * 订单列表
     */
    CommonPage<UserOthersOrderResponse> listAdminOthersOrder(UserOthersOrdeRequest request, PageParamRequest pageRequest);

    UserLjhsOrderNum listLjdrOrderNum(UserLjhsOrdeRequest request);

    UserLjhsOrderNum getOrderStatusNum(UserLjhsOrdeRequest request);

    UserLjhsOrderResponse ljdrOrderDetails(Integer id);
    UserLjhsOrderResponse ljdrOrderDetailsByOrderId(String orderId);


    UserOthersOrderResponse otherOrderDetailsByOrderId( Integer id);


    /**
     * 垃圾订单 编辑
     */
    Boolean updateLjOrder(UserLjhsOrdeRequest request);
    /**
     * 列表（PC）
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return CommonPage<StoreOrderDetailResponse>
     */
    CommonPage<UserLjhsOrderResponse> getAdminList(UserLjhsOrdeRequest request, PageParamRequest pageParamRequest);


    UserLjhsOrder getByOderId(String orderId);

    Boolean autoSendOrder(UserLjhsOrder request);

    Boolean autoSendOrderReply(UserLjhsOrder request);

    /**
     * 用户服务订单状态跟踪
     */
    List<UserLjhsOrderStatus> listUserLjhsOrderStatus(String orderId);


    PageInfo<UserLjhsOrderReplyResponse> getListSericeOrderComment(Integer type,Integer typeId, PageParamRequest pageParamRequest);


    StoreProductReplayCountResponse sericeOrderCommentCount(Integer type);

    MyRecord getH5Count(Integer typeId);

}