package com.hdkj.mall.store.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.constants.Constants;
import com.exception.MallException;
import com.hdkj.mall.store.model.StoreProductReply;
import com.hdkj.mall.store.vo.StoreOrderInfoOldVo;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.service.UserLjhsOrderService;
import com.hdkj.mall.wechat.service.impl.WechatSendMessageForMinService;
import com.hdkj.mall.payment.service.OrderPayService;
import com.hdkj.mall.store.model.StoreOrder;
import com.hdkj.mall.store.model.StoreOrderStatus;
import com.hdkj.mall.store.service.*;
import com.hdkj.mall.user.model.UserLjhsOrder;
import com.hdkj.mall.wechat.vo.WechatSendMessageForOrderCancel;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.hdkj.mall.store.utilService.OrderUtils;
import com.hdkj.mall.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;
import java.util.List;

/**
 * StoreOrderServiceImpl 接口实现
 */
@Service
public class OrderTaskServiceImpl implements OrderTaskService {
    //日志
    private static final Logger logger = LoggerFactory.getLogger(OrderTaskServiceImpl.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private StoreOrderTaskService storeOrderTaskService;

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private WechatSendMessageForMinService wechatSendMessageForMinService;

    @Autowired
    private OrderUtils orderUtils;

    @Autowired
    private StoreOrderStatusService storeOrderStatusService;

    @Autowired
    private StoreOrderInfoService storeOrderInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreProductReplyService storeProductReplyService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private OrderPayService orderPayService;

    @Autowired
    private UserLjhsOrderService userLjhsOrderService;

    /**
     * 用户取消订单
     * @author Mr.Zhou
     * @since 2022-07-09
     */
    @Override
    public void cancelByUser() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_CANCEL_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.cancelByUser | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
//                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(data.toString()));
                boolean result = storeOrderTaskService.cancelByUser(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }else{
                    WechatSendMessageForOrderCancel orderCancel = new WechatSendMessageForOrderCancel(
                            "暂无",DateUtil.nowDateTimeStr(),"","暂无",orderUtils.getPayTypeStrByOrder(storeOrder),
                            orderUtils.getStoreNameAndCarNumString(storeOrder.getId()),storeOrder.getOrderId(),
                            storeOrder.getStatus()+"",storeOrder.getPayPrice()+"",storeOrder.getCreateTime()+"",
                            "MALL","暂无",storeOrder.getOrderId(),"MALL","暂无"
                    );
                    wechatSendMessageForMinService.sendOrderCancelMessage(orderCancel,storeOrder.getUid());
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    private StoreOrder getJavaBeanStoreOrder(Object data) {
        return JSONObject.toJavaObject(JSONObject.parseObject(data.toString()), StoreOrder.class);
    }

    /**
     * 执行 用户退款申请
     * @author Mr.Zhou
     * @since 2022-07-09
     */
    @Override
    public void refundApply() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_REFUND_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.refundApply | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderId = redisUtil.getRightPop(redisKey, 10L);
            if(null == orderId){
                continue;
            }
            try{
                StoreOrder storeOrder = storeOrderService.getById(Integer.valueOf(orderId.toString()));
                if (ObjectUtil.isNull(storeOrder)) {
                    throw new MallException("订单不存在,orderNo = " + orderId);
                }
//                boolean result = storeOrderTaskService.refundApply(storeOrder);
                boolean result = storeOrderTaskService.refundOrder(storeOrder);
                if(!result){
                    logger.error("订单退款错误：result = " + result);
                    redisUtil.lPush(redisKey, orderId);
                }
            }catch (Exception e){
                logger.error("订单退款错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderId);
            }
        }
    }

    /**
     * 执行 用户退款申请
     * @author Mr.Zhou
     * @since 2022-07-09
     */
    @Override
    public void refundApply_fw() {
        String redisKey = Constants.ORDER_FW_TASK_REDIS_KEY_AFTER_REFUND_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.refundApply_fw | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderId = redisUtil.getRightPop(redisKey, 10L);
            if(null == orderId){
                continue;
            }
            try{
                UserLjhsOrder userLjhsOrder = userLjhsOrderService.getByOderId(orderId.toString());
                if (ObjectUtil.isNull(userLjhsOrder)) {
                    throw new MallException("订单不存在,orderNo = " + orderId);
                }
                boolean result = storeOrderTaskService.refundOrder_fw(userLjhsOrder);
                if(!result){
                    logger.error("订单退款错误：result = " + result);
                    redisUtil.lPush(redisKey, orderId);
                }
            }catch (Exception e){
                logger.error("订单退款错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderId);
            }
        }
    }

    /**
     * 完成订单
     * @author Mr.Zhou
     * @since 2022-07-09
     */
    @Override
    public void complete() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.complete | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreOrder storeOrder = getJavaBeanStoreOrder(data);
                boolean result = storeOrderTaskService.complete(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 订单支付成功后置处理
     */
    @Override
    public void orderPaySuccessAfter() {
        String redisKey = Constants.ORDER_TASK_PAY_SUCCESS_AFTER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderPaySuccessAfter | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreOrder storeOrder = storeOrderService.getByOderId(String.valueOf(data));
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("OrderTaskServiceImpl.orderPaySuccessAfter | 订单不存在，orderNo: " + data);
                    throw new MallException("订单不存在，orderNo: " + data);
                }
                boolean result = orderPayService.paySuccess(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 自动取消未支付订单
     */
    @Override
    public void autoCancel() {
        String redisKey = Constants.ORDER_AUTO_CANCEL_KEY;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoCancel | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object data = redisUtil.getRightPop(redisKey, 10L);
            if(null == data){
                continue;
            }
            try{
                StoreOrder storeOrder = storeOrderService.getByOderId(String.valueOf(data));
                if (ObjectUtil.isNull(storeOrder)) {
                    logger.error("OrderTaskServiceImpl.autoCancel | 订单不存在，orderNo: " + data);
                    throw new MallException("订单不存在，orderNo: " + data);
                }
                boolean result = storeOrderTaskService.autoCancel(storeOrder);
                if(!result){
                    redisUtil.lPush(redisKey, data);
                }
            }catch (Exception e){
                e.printStackTrace();
                redisUtil.lPush(redisKey, data);
            }
        }
    }

    /**
     * 订单收货
     */
    @Override
    public void orderReceiving() {
        String redisKey = Constants.ORDER_TASK_REDIS_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.orderReceiving | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object id = redisUtil.getRightPop(redisKey, 10L);
            if(null == id){
                continue;
            }
            try{
                Boolean result = storeOrderTaskService.orderReceiving(Integer.valueOf(id.toString()));
                if(!result){
                    redisUtil.lPush(redisKey, id);
                }
            }catch (Exception e){
                redisUtil.lPush(redisKey, id);
            }
        }
    }

    /**
     * 订单自动完成
     */
    @Override
    public void autoComplete() {
        // 查找所有收获状态订单
        List<StoreOrder> orderList = storeOrderService.findIdAndUidListByReceipt();
        if (CollUtil.isEmpty(orderList)) {
            return ;
        }
        logger.info("OrderTaskServiceImpl.autoComplete | size:0");

        // 根据订单状态表判断订单是否可以自动完成
        for (StoreOrder order : orderList) {
            StoreOrderStatus orderStatus = storeOrderStatusService.getLastByOrderId(order.getId());
            if (!orderStatus.getChangeType().equals("user_take_delivery")) {
                logger.error("订单自动完成：订单记录最后一条不是收货状态，orderId = " + order.getId());
                continue ;
            }
            // 判断是否到自动完成时间（收货时间向后偏移7天）
            String comTime = DateUtil.addDay(orderStatus.getCreateTime(), 7, Constants.DATE_FORMAT);
            int compareDate = DateUtil.compareDate(comTime, DateUtil.nowDateTime(Constants.DATE_FORMAT), Constants.DATE_FORMAT);
            if (compareDate < 0) {
                continue ;
            }

            /**
             * ---------------
             * 自动好评转完成
             * ---------------
             */
            // 获取订单详情
            List<StoreOrderInfoOldVo> orderInfoVoList = storeOrderInfoService.getOrderListByOrderId(order.getId());
            if (CollUtil.isEmpty(orderInfoVoList)) {
                logger.error("订单自动完成：无订单详情数据，orderId = " + order.getId());
                continue;
            }
            List<StoreProductReply> replyList = CollUtil.newArrayList();
            User user = userService.getById(order.getUid());
            // 生成评论
            for (StoreOrderInfoOldVo orderInfo : orderInfoVoList) {
                // 判断是否已评论
                if (orderInfo.getInfo().getIsReply().equals(1)) {
                    continue;
                }
                String replyType = Constants.STORE_REPLY_TYPE_PRODUCT;
                StoreProductReply reply = new StoreProductReply();
                reply.setUid(order.getUid());
                reply.setOid(order.getId());
                reply.setProductId(orderInfo.getProductId());
                reply.setUnique(orderInfo.getUnique());
                reply.setReplyType(replyType);
                reply.setProductScore(5);
                reply.setServiceScore(5);
                reply.setComment("");
                reply.setPics("");
                reply.setNickname(user.getNickname());
                reply.setAvatar(user.getAvatar());
                reply.setSku(orderInfo.getInfo().getSku());
                reply.setCreateTime(DateUtil.nowDateTime());
                replyList.add(reply);
            }
            order.setStatus(Constants.ORDER_STATUS_INT_COMPLETE);
            Boolean execute = transactionTemplate.execute(e -> {
                storeOrderService.updateById(order);
                storeProductReplyService.saveBatch(replyList);
                return Boolean.TRUE;
            });
            if (execute) {
                redisUtil.lPush(Constants.ORDER_TASK_REDIS_KEY_AFTER_COMPLETE_BY_USER, order.getId());
            } else {
                logger.error("订单自动完成：更新数据库失败，orderId = " + order.getId());
            }
        }
    }

    /**
     * 执行 自动派送订单
     */
    @Override
    public void autoSendOrder() {
        String redisKey = Constants.ORDER_FW_TASK_CREATE_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoSendOrder | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderId = redisUtil.getRightPop(redisKey, 10L);
            if(null == orderId){
                continue;
            }
            try{
                UserLjhsOrder userLjhsOrder = userLjhsOrderService.getByOderId(orderId.toString());
                if (ObjectUtil.isNull(userLjhsOrder)) {
                    throw new MallException("订单不存在,orderNo = " + orderId);
                }
                boolean result = userLjhsOrderService.autoSendOrder(userLjhsOrder);
                if(!result){
                    logger.error("订单自动派送错误：result = " + result);
                    redisUtil.lPush(redisKey, orderId);
                }
            }catch (Exception e){
                logger.error("订单自动派送错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderId);
            }
        }
    }

    /**
     * 执行 服务订单7天自动评价
     */
    @Override
    public void autoSendOrderReply() {
        String redisKey = Constants.ORDER_FW_TASK_PJ_KEY_AFTER_TAKE_BY_USER;
        Long size = redisUtil.getListSize(redisKey);
        logger.info("OrderTaskServiceImpl.autoSendOrderReply | size:" + size);
        if(size < 1){
            return;
        }
        for (int i = 0; i < size; i++) {
            //如果10秒钟拿不到一个数据，那么退出循环
            Object orderId = redisUtil.getRightPop(redisKey, 10L);
            if(null == orderId){
                continue;
            }
            try{
                UserLjhsOrder userLjhsOrder = userLjhsOrderService.getByOderId(orderId.toString());
                if (ObjectUtil.isNull(userLjhsOrder)) {
                    throw new MallException("订单不存在,orderNo = " + orderId);
                }

                int time =  DateUtil.daysBetween(userLjhsOrder.getWtime(),new Date());
                if(time>=7){
                    boolean result = userLjhsOrderService.autoSendOrderReply(userLjhsOrder);
                    if(!result){
                        logger.error("服务订单自动评价错误：result = " + result);
                        redisUtil.lPush(redisKey, orderId);
                    }
                }
            }catch (Exception e){
                logger.error("服务订单自动评价错误：" + e.getMessage());
                redisUtil.lPush(redisKey, orderId);
            }
        }
    }
}
