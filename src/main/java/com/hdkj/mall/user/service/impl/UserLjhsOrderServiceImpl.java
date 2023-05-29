package com.hdkj.mall.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.IntegralRecordConstants;
import com.constants.UserConstants;
import com.exception.MallException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.front.response.StoreProductReplayCountResponse;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.user.dao.UserLjhsOrderDao;
import com.hdkj.mall.user.request.UserLjhsOrdeRequest;
import com.hdkj.mall.user.response.UserLjhsOrderReplyResponse;
import com.hdkj.mall.user.response.UserLjhsOrderResponse;
import com.hdkj.mall.user.response.UserOthersOrderResponse;
import com.hdkj.mall.user.service.*;
import com.hdkj.mall.wechat.service.TemplateMessageService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.user.dao.UserLjhsOrderStatusDao;
import com.hdkj.mall.user.dao.UserOthersOrderDao;
import com.hdkj.mall.user.model.*;
import com.hdkj.mall.user.request.UserOthersOrdeRequest;
import com.hdkj.mall.user.response.UserLjhsOrderNum;
import com.utils.DateUtil;
import com.utils.RedisUtil;
import com.utils.vo.dateLimitUtilVo;
import com.hdkj.mall.user.dao.UserLjhsOrderReplyDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * UserLjhsOrderServiceImpl 用户垃圾回收订单表
 */
@Service
public class UserLjhsOrderServiceImpl extends ServiceImpl<UserLjhsOrderDao, UserLjhsOrder> implements UserLjhsOrderService {

    @Resource
    private UserLjhsOrderDao userLjhsOrderDao;
    @Resource
    private UserOthersOrderDao userOthersOrderDao;
    @Resource
    private UserLjhsOrderStatusDao userLjhsOrderStatusDao;
    @Resource
    private UserLjhsOrderReplyDao userLjhsOrderReplyDao;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private UserIntegralRecordService userIntegralRecordService;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private UserBillService userBillService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TemplateMessageService templateMessageService;

    @Autowired
    private UserTokenService userTokenService;


    /**
     * 用户垃圾代仍订单创建
     */
    @Override
    public Boolean createLjdrOder(UserLjhsOrdeRequest userLjhsOrdeRequest) {
        UserLjhsOrder userLjhsOrder = new UserLjhsOrder();
        BeanUtils.copyProperties(userLjhsOrdeRequest, userLjhsOrder);
//        userLjhsOrder.setOrderId(String.valueOf(System.currentTimeMillis()));
        //详情图
        userLjhsOrder.setDetailImages(systemAttachmentService.clearPrefix(userLjhsOrder.getDetailImages()));
        userLjhsOrder.setDetailImages1(systemAttachmentService.clearPrefix(userLjhsOrder.getDetailImages1()));
        userLjhsOrder.setKzzd(systemAttachmentService.clearPrefix(userLjhsOrder.getKzzd()));

        if(save(userLjhsOrder)){
            // 派单task
            if(userLjhsOrder.getOrderType() !=0){
                redisUtil.lPush(Constants.ORDER_FW_TASK_CREATE_KEY_AFTER_TAKE_BY_USER, userLjhsOrdeRequest.getOrderId());
            }
            insertLjdrOderStatus(userLjhsOrder);

            sendMessage(userLjhsOrder);
            return true;
        }else{
            return false;
        }
    }


    public void insertLjdrOderStatus(UserLjhsOrder userLjhsOrder){
        UserLjhsOrderStatus userLjhsOrderStatus =  new UserLjhsOrderStatus();
        userLjhsOrderStatus.setOrderId(userLjhsOrder.getOrderId());
        userLjhsOrderStatus.setStatus(userLjhsOrder.getStatus());
        userLjhsOrderStatusDao.insert(userLjhsOrderStatus);
    }

    public Boolean updateLjdrOder_xy(UserLjhsOrder userLjhsOrder){
       if(userLjhsOrder.getStatus()==0){
           //洗衣-在线支付-处理
           if(userLjhsOrder.getPayType()!=null && userLjhsOrder.getPayType()==0 && userLjhsOrder.getPaid()==1){
               String title = IntegralRecordConstants.BROKERAGE_RECORD_TITLE_XY;
               User user = userService.getById(userLjhsOrder.getUid());
               UserBill userBill = new UserBill();
               userBill.setTitle(title);
               userBill.setUid(userLjhsOrder.getUid());
               userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
               userBill.setType(Constants.USER_BILL_TYPE_PAY_ORDER);
               userBill.setNumber(new BigDecimal(userLjhsOrder.getPayPrice()));
               userBill.setLinkId(userLjhsOrder.getId()+"");
               userBill.setBalance(user.getNowMoney());
               if(userLjhsOrder.getPayType1()==0){
                   userBill.setMark("余额支付" + userLjhsOrder.getPayPrice() + "元完成订单");
               }else{
                   userBill.setMark("微信支付" + userLjhsOrder.getPayPrice() + "元完成订单");
               }
               userBillService.save(userBill);
               if(userLjhsOrder.getPayType1()==0){
                   userService.operationNowMoney(user.getUid(), new BigDecimal(userLjhsOrder.getPayPrice()),user.getNowMoney(), "sub");
               }
           }
       }
       if(userLjhsOrder.getStatus()==7){
           //洗衣-在线支付退款-处理
           if(userLjhsOrder.getPayType()!=null && userLjhsOrder.getPayType()==0  && userLjhsOrder.getPaid()==1){
               //用户
               User user = userService.getById(userLjhsOrder.getUid());
               if(userLjhsOrder.getPayType1()==0){ //余额支付
                   //新增日志
                   userBillService.saveRefundBill_fw(userLjhsOrder, user);
                   // 更新用户金额
                   userService.operationNowMoney(user.getUid(), new BigDecimal(userLjhsOrder.getPayPrice()), user.getNowMoney(), "add");
                   userLjhsOrder.setRefundStatus(2);
               }else{
                   userLjhsOrder.setRefundStatus(1);
                   // 退款task
                   redisUtil.lPush(Constants.ORDER_FW_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, userLjhsOrder.getOrderId());
               }
           }
       }
        if(userLjhsOrder.getStatus()==31){ //商家退回
            //洗衣-在线支付退款-处理
            if(userLjhsOrder.getPayType()!=null && userLjhsOrder.getPayType()==0  && userLjhsOrder.getPaid()==1){
                //用户
                User user = userService.getById(userLjhsOrder.getUid());
                if(userLjhsOrder.getPayType1()==0){ //余额支付
                    //新增日志
                    userBillService.saveRefundBill_fw(userLjhsOrder, user);
                    // 更新用户金额
                    userService.operationNowMoney(user.getUid(), new BigDecimal(userLjhsOrder.getPayPrice()), user.getNowMoney(), "add");
                    userLjhsOrder.setRefundStatus(2);
                }else{
                    userLjhsOrder.setRefundStatus(1);
                    // 退款task
                    redisUtil.lPush(Constants.ORDER_FW_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, userLjhsOrder.getOrderId());
                }
            }
        }
        if (userLjhsOrder.getStatus() == 6) {
            userLjhsOrder.setWtime(new Date());
        }
        if(updateById(userLjhsOrder)){
            if(userLjhsOrder.getStatus()==0 && userLjhsOrder.getPaid() ==1){
                redisUtil.lPush(Constants.ORDER_FW_TASK_CREATE_KEY_AFTER_TAKE_BY_USER, userLjhsOrder.getOrderId());
            }
            if(userLjhsOrder.getStatus()!=0){
                insertLjdrOderStatus(userLjhsOrder);
            }
            if (userLjhsOrder.getStatus() == 6) {
                redisUtil.lPush(Constants.ORDER_FW_TASK_PJ_KEY_AFTER_TAKE_BY_USER, userLjhsOrder.getOrderId());
            }
            return true;
        }else{
            return false;
        }
    }
    public Boolean updateLjdrOder_hs(UserLjhsOrder userLjhsOrder){
        if(userLjhsOrder.getStatus() == 2){
            //维修-在线支付-处理
            if(userLjhsOrder.getPayType()!=null && userLjhsOrder.getPayType()==0  && userLjhsOrder.getPaid()==1){
                String title = IntegralRecordConstants.BROKERAGE_RECORD_TITLE_HS;
                User user = userService.getById(userLjhsOrder.getUid());
                UserBill userBill = new UserBill();
                userBill.setTitle(title);
                userBill.setPm(1);
                userBill.setUid(userLjhsOrder.getUid());
                userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
                userBill.setType(Constants.USER_BILL_TYPE_PAY_ORDER);
                userBill.setNumber(new BigDecimal(userLjhsOrder.getPayPrice()));
                userBill.setLinkId(userLjhsOrder.getId()+"");
                userBill.setBalance(user.getNowMoney());
                userBill.setMark("余额收入" + userLjhsOrder.getPayPrice() + "元完成订单");
                userBillService.save(userBill);
                userService.operationNowMoney(user.getUid(), new BigDecimal(userLjhsOrder.getPayPrice()),user.getNowMoney(), "add");
            }
        }
        if (userLjhsOrder.getStatus() == 6) {
            userLjhsOrder.setWtime(new Date());
        }
        if(updateById(userLjhsOrder)){
            insertLjdrOderStatus(userLjhsOrder);

            if (userLjhsOrder.getStatus() == 6) {
                redisUtil.lPush(Constants.ORDER_FW_TASK_PJ_KEY_AFTER_TAKE_BY_USER, userLjhsOrder.getOrderId());
            }
            return true;
        }else{
            return false;
        }
    }
    public Boolean updateLjdrOder_wx(UserLjhsOrder userLjhsOrder){
        if(userLjhsOrder.getStatus() == 32){
            //维修-在线支付-处理
            if(userLjhsOrder.getPayType()==0  && userLjhsOrder.getPaid()==1){
                String title = IntegralRecordConstants.BROKERAGE_RECORD_TITLE_WX;
                User user = userService.getById(userLjhsOrder.getUid());
                UserBill userBill = new UserBill();
                userBill.setTitle(title);
                userBill.setUid(userLjhsOrder.getUid());
                userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
                userBill.setType(Constants.USER_BILL_TYPE_PAY_ORDER);
                userBill.setNumber(new BigDecimal(userLjhsOrder.getPayPrice()));
                userBill.setLinkId(userLjhsOrder.getId()+"");
                userBill.setBalance(user.getNowMoney());

                if(userLjhsOrder.getPayType1()==0){
                    userBill.setMark("余额支付" + userLjhsOrder.getPayPrice() + "元完成订单");
                }else{
                    userBill.setMark("微信支付" + userLjhsOrder.getPayPrice() + "元完成订单");
                }
                userBillService.save(userBill);
                if(userLjhsOrder.getPayType1()==0){
                    userService.operationNowMoney(user.getUid(), new BigDecimal(userLjhsOrder.getPayPrice()),user.getNowMoney(), "sub");
                }
            }
        }
        if(userLjhsOrder.getStatus()==7){
            //洗衣-在线支付退款-处理
            if(userLjhsOrder.getPayType()!=null && userLjhsOrder.getPayType()==0  && userLjhsOrder.getPaid()==1){
                //用户
                User user = userService.getById(userLjhsOrder.getUid());
                if(userLjhsOrder.getPayType1()==0){ //余额支付
                    //新增日志
                    userBillService.saveRefundBill_fw(userLjhsOrder, user);
                    // 更新用户金额
                    userService.operationNowMoney(user.getUid(), new BigDecimal(userLjhsOrder.getPayPrice()), user.getNowMoney(), "add");
                    userLjhsOrder.setRefundStatus(2);
                }else{
                    userLjhsOrder.setRefundStatus(1);
                    // 退款task
                    redisUtil.lPush(Constants.ORDER_FW_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, userLjhsOrder.getOrderId());
                }
            }
        }
        if(userLjhsOrder.getStatus()==31){ //商家退回
            //洗衣-在线支付退款-处理
            if(userLjhsOrder.getPayType()!=null && userLjhsOrder.getPayType()==0  && userLjhsOrder.getPaid()==1){
                //用户
                User user = userService.getById(userLjhsOrder.getUid());
                if(userLjhsOrder.getPayType1()==0){ //余额支付
                    //新增日志
                    userBillService.saveRefundBill_fw(userLjhsOrder, user);
                    // 更新用户金额
                    userService.operationNowMoney(user.getUid(), new BigDecimal(userLjhsOrder.getPayPrice()), user.getNowMoney(), "add");
                    userLjhsOrder.setRefundStatus(2);
                }else{
                    userLjhsOrder.setRefundStatus(1);
                    // 退款task
                    redisUtil.lPush(Constants.ORDER_FW_TASK_REDIS_KEY_AFTER_REFUND_BY_USER, userLjhsOrder.getOrderId());
                }
            }
        }
        if (userLjhsOrder.getStatus() == 6) {
            userLjhsOrder.setWtime(new Date());
        }
        if(updateById(userLjhsOrder)){
            insertLjdrOderStatus(userLjhsOrder);

            if (userLjhsOrder.getStatus() == 6) {
                redisUtil.lPush(Constants.ORDER_FW_TASK_PJ_KEY_AFTER_TAKE_BY_USER, userLjhsOrder.getOrderId());
            }
            return true;
        }else{
            return false;
        }
    }


    /**
     * 用户垃圾代仍订单更新
     */
    @Override
    public Boolean updateLjdrOder(UserLjhsOrdeRequest userLjhsOrdeRequest) {
        UserLjhsOrder userLjhsOrder = new UserLjhsOrder();
        BeanUtils.copyProperties(userLjhsOrdeRequest, userLjhsOrder);
        userLjhsOrder.setUpdateTime(new Date());
        //详情图
        userLjhsOrder.setDetailImages(systemAttachmentService.clearPrefix(userLjhsOrder.getDetailImages()));
        userLjhsOrder.setDetailImages1(systemAttachmentService.clearPrefix(userLjhsOrder.getDetailImages1()));
        userLjhsOrder.setCauseImage(systemAttachmentService.clearPrefix(userLjhsOrder.getCauseImage()));
        userLjhsOrder.setKzzd(systemAttachmentService.clearPrefix(userLjhsOrder.getKzzd()));


        if(userLjhsOrder.getOrderType()==0){
            if(updateLjdrOder_xy(userLjhsOrder)){
                sendMessage(userLjhsOrder);
                return true;
            }else{
                return false;
            }
        }else if(userLjhsOrder.getOrderType()==1){
            if(updateLjdrOder_hs(userLjhsOrder)){
                sendMessage(userLjhsOrder);
                return true;
            }else{
                return false;
            }
        }else if(userLjhsOrder.getOrderType()==2){
            if(updateLjdrOder_wx(userLjhsOrder)){
                sendMessage(userLjhsOrder);
                return true;
            }else{
                return false;
            }
        }else{
            throw new MallException("订单类型错误");
        }
    }

    public void sendMessage(UserLjhsOrder userLjhsOrder){
        try {

            if(userLjhsOrder.getStatus()>0){
                boolean flag1 = false;
                boolean flag2 = false;

                String str1 =userLjhsOrder.getOrderId(); //订单编号
                String str2 ="订单"; //订单类型
                String str3 ="已下单"; //订单状态
                String str33 ="已下单"; //订单状态
                if(userLjhsOrder.getOrderType()==0){
                    str2 = "洗衣订单";
                    switch (userLjhsOrder.getStatus()) {
                        case 0:
                            str3 = "未接单";
                            break;
                        case 1:
                            str3 = "已接单";
                            str33 ="待处理";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 2:
                            str3 = "订单已确认";
                            str33 = "订单已确认";
                            break;
                        case 3:
                            str3 = "商家已确认";
                            str33 = "商家已确认";
                            break;
                        case 31:
                            str3 = "商家已退回";
                            str33 = "商家已退回";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 4:
                            str3 = "商家已发货";
                            str33 = "商家已发货";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 5:
                            str3 = "订单待收货";
                            str33 = "订单待收货";
                            break;
                        case 6:
                            str3 = "订单待评价";
                            str33 = "用户已收货";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 7:
                            str3 = "订单已取消";
                            str33 = "订单已取消";
                            break;
                        case 8:
                            str3 = "订单已完成";
                            str33 = "订单已完成";
                            break;
                        default:
                            break;
                    }
                }else if(userLjhsOrder.getOrderType()==1){
                    str2 = "回收订单";
                    switch (userLjhsOrder.getStatus()) {
                        case 0:
                            str3 = "未接单";
                            break;
                        case 1:
                            str3 = "已接单";
                            str33 ="待处理";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 2:
                            str3 = "待确认";
                            str33 = "待用户确认";
                            break;
                        case 6:
                            str3 = "待评价";
                            str33 = "用户未评价";
                            break;
                        case 7:
                            str3 = "订单已取消";
                            str33 = "订单已取消";
                            break;
                        case 8:
                            str3 = "订单已完成";
                            str33 = "订单已完成";
                            break;
                        default:
                            break;
                    }
                }else if(userLjhsOrder.getOrderType()==2){
                    str2 = "维修订单";
                    switch (userLjhsOrder.getStatus()) {
                        case 0:
                            str3 = "已下单，未接单";
                            break;
                        case 1:
                            str3 = "已接单";
                            str33 ="待处理";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 2:
                            str3 = "订单已确认";
                            str33 = "订单已确认";
                            break;
                        case 3:
                            str3 = "待付款";
                            str33 = "用户待付款";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 31:
                            str3 = "商家已退回";
                            str33 = "商家已退回";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 33:
                            str3 = "已付款";
                            str33 = "用户已付款";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 4:
                            str3 = "商家已发货";
                            str33 = "商家已发货";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 5:
                            str3 = "订单待收货";
                            str33 = "用户未确认收货";
                            break;
                        case 6:
                            str3 = "订单待评价";
                            str33 = "用户已收货";
                            flag1 = true;
                            flag2 = true;
                            break;
                        case 7:
                            str3 = "订单已取消";
                            str33 = "订单已取消";
                            break;
                        case 8:
                            str3 = "订单已完成";
                            str33 = "订单已完成";
                            break;
                        default:
                            break;
                    }
                }
                if(flag1){
                    // 小程序发送订阅消息
                    UserToken userToken = userTokenService.getTokenByUserId(userLjhsOrder.getUid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
                    HashMap<String, String> temMap = new HashMap<>();
                    // 组装数据
                    temMap.put("character_string6",  str1); //订单编号
                    temMap.put("phrase1", str3);//订单状态
                    temMap.put("thing5", str2);//备注-订单类型
                    templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_YUEYU, temMap, userToken.getToken());

                }
                if(flag2){
                    // 小程序发送订阅消息
                    UserToken userToken = userTokenService.getTokenByUserId(userLjhsOrder.getCuid(), UserConstants.USER_TOKEN_TYPE_ROUTINE);
                    HashMap<String, String> temMap = new HashMap<>();
                    // 组装数据
                    temMap.put("character_string1",  str1); //任务单号
                    temMap.put("thing2", str2); //任务名称
                    temMap.put("phrase3", str33);//任务状态
                    templateMessageService.pushMiniTemplateMessage(Constants.WE_CHAT_PROGRAM_TEMP_KEY_ORDER_RECEVIE, temMap, userToken.getToken());
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 用户服务订单评价更新
     */
    @Override
    public Boolean sericeOrderComment(UserLjhsOrderReply userLjhsOrderReply) {
        userLjhsOrderReply.setPics(systemAttachmentService.clearPrefix(userLjhsOrderReply.getPics()));
        UserLjhsOrder userLjhsOrder =getByOderId(userLjhsOrderReply.getOrderId());
        if(userLjhsOrder.getOrderType()==null){
            throw new MallException("订单不存在");
        }
        User user = userService.getById(userLjhsOrder.getUid());
        if(user==null){
            throw new MallException("用户不存在");
        }
        String replyType = "";
        if(userLjhsOrder.getOrderType()==0){
            replyType ="洗衣";
        }else if (userLjhsOrder.getOrderType()==1){
            replyType ="回收";
        }else{
            replyType ="维修";
        }
        userLjhsOrderReply.setReplyType(replyType);
        userLjhsOrderReply.setType(userLjhsOrder.getOrderType());
        userLjhsOrderReply.setUid(user.getUid());
        userLjhsOrderReply.setNickname(user.getNickname());
        userLjhsOrderReply.setAvatar(user.getAvatar());
        if(userLjhsOrderReplyDao.insert(userLjhsOrderReply)>0){
            userLjhsOrder.setStatus(8);
            userLjhsOrder.setUpdateTime(new Date());
            UserLjhsOrdeRequest request = new UserLjhsOrdeRequest();
            BeanUtils.copyProperties(userLjhsOrder,request);
            updateLjdrOder(request);
            return true;
        }else{
            return false;
        }
    }

    /**
     * 订单列表
     * @param pageRequest 分页
     * @return CommonPage<OrderDetailResponse>
     */
    @Override
    public CommonPage<UserLjhsOrderResponse> listLjdrOrder(UserLjhsOrdeRequest request, PageParamRequest pageRequest) {

        PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserLjhsOrder> lqw = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lqw.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lqw.eq(UserLjhsOrder::getCuid, request.getCuid());
        }

        if(request.getStatus()==0){ //服务中
            List list = new ArrayList();
            list.add(1);
            list.add(6);
            list.add(7);
            list.add(8);
            lqw.notIn(UserLjhsOrder::getStatus, list);
        }else if(request.getStatus()==1){ //已接单
            lqw.eq(UserLjhsOrder::getStatus, 1);
        }else if(request.getStatus()==2){ //已完成
            lqw.eq(UserLjhsOrder::getStatus, 8);
        }else if(request.getStatus()==3){ // 已取消
            lqw.eq(UserLjhsOrder::getStatus, 7);
        }else if(request.getStatus()==4){ //全部
//            lqw.eq(UserLjhsOrder::getStatus, 7);
        }else if(request.getStatus()==5){ //待评价
            lqw.eq(UserLjhsOrder::getStatus, 6);
        }else{//全部
        }

        lqw.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        lqw.orderByDesc(UserLjhsOrder::getId);
        List<UserLjhsOrder> orderList = userLjhsOrderDao.selectList(lqw);
        List<UserLjhsOrderResponse> ljhsOrderResponses = new ArrayList<>();
        for(UserLjhsOrder userLjhsOrder :orderList){
            UserLjhsOrderResponse userLjhsOrderResponse = new UserLjhsOrderResponse();
            BeanUtils.copyProperties(userLjhsOrder, userLjhsOrderResponse);
            Category category =  categoryService.getById(userLjhsOrder.getLjlxId()); //垃圾类型
            if(category !=null){
                userLjhsOrderResponse.setLjlxName(category.getName());
                userLjhsOrderResponse.setExtra(category.getExtra());
            }
            category =  categoryService.getById(userLjhsOrder.getPid()); //小区
            if(category !=null){
                userLjhsOrderResponse.setPName(category.getName());
            }
            ljhsOrderResponses.add(userLjhsOrderResponse);
        }
        CommonPage<UserLjhsOrderResponse> userLjhsOrderCommonPage = CommonPage.restPage(ljhsOrderResponses);
        return userLjhsOrderCommonPage;
    }

    /**
     * 列表
     * @param request 请求参数
     * @param pageParamRequest 分页类参数
     * @return CommonPage<StoreOrderDetailResponse>
     */
    @Override
    public CommonPage<UserLjhsOrderResponse> getAdminList(UserLjhsOrdeRequest request, PageParamRequest pageParamRequest) {
        Page<Object> startPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserLjhsOrder> lqw = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }

        if (ObjectUtil.isNotNull(request.getOrderType())) {
            lqw.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        }
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }
        if (ObjectUtil.isNotNull(request.getStatus())) {
            if(request.getStatus()!=-1){
                if(request.getStatus()==-2){
                    lqw.eq(UserLjhsOrder::getIsDel, 1);
                }else if(request.getStatus()==3){
                    lqw.eq(UserLjhsOrder::getIsDel, 0);
                    lqw.eq(UserLjhsOrder::getStatus, 7);
                }else if(request.getStatus()==4){
                    List list = new ArrayList();
                    list.add(6);
                    list.add(0);
                    list.add(7);
                    list.add(8);
                    lqw.notIn(UserLjhsOrder::getStatus, list);
                }else if(request.getStatus()==2){
                    lqw.eq(UserLjhsOrder::getStatus, 8);
                }else if(request.getStatus()==5){
                    lqw.eq(UserLjhsOrder::getStatus, 6);
                }else{
                    lqw.eq(UserLjhsOrder::getStatus, request.getStatus());
                }
            }
        }
        lqw.orderByDesc(UserLjhsOrder::getId);
        List<UserLjhsOrder> orderList = userLjhsOrderDao.selectList(lqw);
        List<UserLjhsOrderResponse> ljhsOrderResponses = new ArrayList<>();
        for(UserLjhsOrder userLjhsOrder :orderList){
            UserLjhsOrderResponse userLjhsOrderResponse = new UserLjhsOrderResponse();
            BeanUtils.copyProperties(userLjhsOrder, userLjhsOrderResponse);
            Category category =  categoryService.getById(userLjhsOrder.getLjlxId()); //垃圾类型
            if(category !=null){
                userLjhsOrderResponse.setLjlxName(category.getName());
                userLjhsOrderResponse.setExtra(category.getExtra());
            }
            category =  categoryService.getById(userLjhsOrder.getPid()); //小区
            if(category !=null){
                userLjhsOrderResponse.setPName(category.getName());
            }
            ljhsOrderResponses.add(userLjhsOrderResponse);
        }
        return CommonPage.restPage(CommonPage.copyPageInfo(startPage, ljhsOrderResponses));
    }
    @Override
    public UserLjhsOrderNum getOrderStatusNum(UserLjhsOrdeRequest request) {

        UserLjhsOrderNum userLjhsOrderNum = new UserLjhsOrderNum();
        LambdaQueryWrapper<UserLjhsOrder> lqw4 = new LambdaQueryWrapper<>();
        lqw4.eq(UserLjhsOrder::getOrderType, request.getOrderType());

        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw4.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }

        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw4.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count4= userLjhsOrderDao.selectCount(lqw4);
        userLjhsOrderNum.setAllOrderNum(count4);

        LambdaQueryWrapper<UserLjhsOrder> lqw5 = new LambdaQueryWrapper<>();
        lqw5.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        lqw5.eq(UserLjhsOrder::getIsDel, 1);
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw5.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw5.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count5= userLjhsOrderDao.selectCount(lqw5);
        userLjhsOrderNum.setDelOrderNum(count5);

        LambdaQueryWrapper<UserLjhsOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserLjhsOrder::getStatus, 0);
        lqw.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count= userLjhsOrderDao.selectCount(lqw);
        userLjhsOrderNum.setNoOrderNum(count);

        LambdaQueryWrapper<UserLjhsOrder> lqw11 = new LambdaQueryWrapper<>();
        lqw11.eq(UserLjhsOrder::getStatus, 1);
        lqw11.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw11.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }

        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw11.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count11= userLjhsOrderDao.selectCount(lqw11);
        userLjhsOrderNum.setHaveOrderNum(count11);


        LambdaQueryWrapper<UserLjhsOrder> lqw12 = new LambdaQueryWrapper<>();
        lqw12.eq(UserLjhsOrder::getStatus, 6);
        lqw12.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw12.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }

        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw12.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count12= userLjhsOrderDao.selectCount(lqw12);
        userLjhsOrderNum.setNoPjOrderNum(count12);

        LambdaQueryWrapper<UserLjhsOrder> lqw1 = new LambdaQueryWrapper<>();
        List list = new ArrayList();
        list.add(6);
        list.add(0);
        list.add(7);
        list.add(8);
        lqw1.notIn(UserLjhsOrder::getStatus, list);

        lqw1.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw1.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw1.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count1= userLjhsOrderDao.selectCount(lqw1);
        userLjhsOrderNum.setSerOrderNum(count1);



        LambdaQueryWrapper<UserLjhsOrder> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(UserLjhsOrder::getStatus, 8);
        lqw2.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw2.like(UserLjhsOrder::getOrderCode, request.getOrderCode());

        }
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw2.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count2= userLjhsOrderDao.selectCount(lqw2);
        userLjhsOrderNum.setYesOrderNum(count2);

        LambdaQueryWrapper<UserLjhsOrder> lqw3 = new LambdaQueryWrapper<>();
        lqw3.eq(UserLjhsOrder::getStatus, 7);
        lqw3.eq(UserLjhsOrder::getIsDel, 0);
        lqw3.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        if (null!=request.getOrderCode() && !request.getOrderCode().equals("")) {
            lqw3.like(UserLjhsOrder::getOrderCode, request.getOrderCode());
        }
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw3.between(UserLjhsOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        Integer count3= userLjhsOrderDao.selectCount(lqw3);
        userLjhsOrderNum.setCancelOrderNum(count3);
        return userLjhsOrderNum;
    }

    @Override
    public UserLjhsOrderNum listLjdrOrderNum(UserLjhsOrdeRequest request) {

        UserLjhsOrderNum userLjhsOrderNum = new UserLjhsOrderNum();

        LambdaQueryWrapper<UserLjhsOrder> lq = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lq.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lq.eq(UserLjhsOrder::getCuid, request.getCuid());
        }
        lq.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        Integer ct= userLjhsOrderDao.selectCount(lq);
        userLjhsOrderNum.setAllOrderNum(ct);

        LambdaQueryWrapper<UserLjhsOrder> lq1 = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lq1.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lq1.eq(UserLjhsOrder::getCuid, request.getCuid());
        }
        lq1.eq(UserLjhsOrder::getStatus, 6);
        lq1.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        Integer ct1= userLjhsOrderDao.selectCount(lq1);
        userLjhsOrderNum.setNoPjOrderNum(ct1);

        LambdaQueryWrapper<UserLjhsOrder> lqw = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lqw.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lqw.eq(UserLjhsOrder::getCuid, request.getCuid());
        }
        lqw.eq(UserLjhsOrder::getStatus, 0);
        lqw.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        Integer count= userLjhsOrderDao.selectCount(lqw);
        userLjhsOrderNum.setNoOrderNum(count);

        LambdaQueryWrapper<UserLjhsOrder> lqw22 = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lqw22.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lqw22.eq(UserLjhsOrder::getCuid, request.getCuid());
        }
        lqw22.eq(UserLjhsOrder::getStatus, 1);
        lqw22.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        Integer count22= userLjhsOrderDao.selectCount(lqw22);
        userLjhsOrderNum.setHaveOrderNum(count22);


        LambdaQueryWrapper<UserLjhsOrder> lqw1 = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lqw1.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lqw1.eq(UserLjhsOrder::getCuid, request.getCuid());
        }
//        lqw1.eq(UserLjhsOrder::getStatus, 1);
        List list = new ArrayList();
        list.add(1);
        list.add(6);
        list.add(7);
        list.add(8);
        lqw1.notIn(UserLjhsOrder::getStatus, list);
        lqw1.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        Integer count1= userLjhsOrderDao.selectCount(lqw1);
        userLjhsOrderNum.setSerOrderNum(count1);

        LambdaQueryWrapper<UserLjhsOrder> lqw2 = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lqw2.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lqw2.eq(UserLjhsOrder::getCuid, request.getCuid());
        }
        lqw2.eq(UserLjhsOrder::getStatus, 8);
        lqw2.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        Integer count2= userLjhsOrderDao.selectCount(lqw2);
        userLjhsOrderNum.setYesOrderNum(count2);

        LambdaQueryWrapper<UserLjhsOrder> lqw3 = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lqw3.eq(UserLjhsOrder::getUid, request.getUid());
        }
        if (request.getCuid()!=null && request.getCuid()!=0) {
            lqw3.eq(UserLjhsOrder::getCuid, request.getCuid());
        }
        lqw3.eq(UserLjhsOrder::getStatus, 7);
        lqw3.eq(UserLjhsOrder::getIsDel, 0);
        lqw3.eq(UserLjhsOrder::getOrderType, request.getOrderType());
        Integer count3= userLjhsOrderDao.selectCount(lqw3);
        userLjhsOrderNum.setCancelOrderNum(count3);
        return userLjhsOrderNum;
    }

    @Override
    public UserLjhsOrderResponse ljdrOrderDetails(Integer id) {

        LambdaQueryWrapper<UserLjhsOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserLjhsOrder::getId, id);
        UserLjhsOrder userLjhsOrder= userLjhsOrderDao.selectOne(lqw);

        UserLjhsOrderResponse userLjhsOrderResponse = new UserLjhsOrderResponse();
        BeanUtils.copyProperties(userLjhsOrder, userLjhsOrderResponse);
        Category category =  categoryService.getById(userLjhsOrder.getLjlxId()); //垃圾类型
        if(category !=null){
            userLjhsOrderResponse.setLjlxName(category.getName());
            userLjhsOrderResponse.setExtra(category.getExtra());
        }
        category =  categoryService.getById(userLjhsOrder.getPid()); //小区
        if(category !=null){
            userLjhsOrderResponse.setPName(category.getName());
        }
        return userLjhsOrderResponse;
    }


    @Override
    public UserLjhsOrderResponse ljdrOrderDetailsByOrderId(String orderId) {

        LambdaQueryWrapper<UserLjhsOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserLjhsOrder::getOrderId, orderId);
        UserLjhsOrder userLjhsOrder= userLjhsOrderDao.selectOne(lqw);

        UserLjhsOrderResponse userLjhsOrderResponse = new UserLjhsOrderResponse();
        BeanUtils.copyProperties(userLjhsOrder, userLjhsOrderResponse);
        User user =  userService.getById(userLjhsOrder.getUid()); //垃圾类型
        if(user !=null){
            userLjhsOrderResponse.setUName(user.getNickname());
            userLjhsOrderResponse.setUPhone(user.getPhone());
        }
        Category category =  categoryService.getById(userLjhsOrder.getLjlxId()); //垃圾类型
        if(category !=null){
            userLjhsOrderResponse.setLjlxName(category.getName());
            userLjhsOrderResponse.setExtra(category.getExtra());
        }
        if(userLjhsOrder.getPid()!=null && userLjhsOrder.getPid() !=0){
            category =  categoryService.getById(userLjhsOrder.getPid()); //小区
            if(category !=null){
                userLjhsOrderResponse.setPName(category.getName());
            }
        }
        return userLjhsOrderResponse;
    }

    @Override
    public Boolean updateLjOrder(UserLjhsOrdeRequest request) {
        UserLjhsOrder userLjhsOrder =  getById(request.getId());
        if(userLjhsOrder==null){
            throw new MallException("该订单不存在！");
        }
        if(userLjhsOrder.getIsDel()==1){
            throw new MallException("该订单已删除！");
        }
        if(userLjhsOrder.getStatus()==7){
            throw new MallException("该订单已取消！");
        }
        if(userLjhsOrder.getStatus()==8){
            throw new MallException("该订单已完结！");
        }
        userLjhsOrder.setStatus(request.getStatus());
        if(userLjhsOrder.getOrderType()==2){
            if(userLjhsOrder.getStatus() !=null && userLjhsOrder.getStatus()==3){
                userLjhsOrder.setPayPrice(request.getPayPrice());
            }
            if(userLjhsOrder.getStatus() !=null && userLjhsOrder.getStatus()==31){
                userLjhsOrder.setCause(request.getCause());
                userLjhsOrder.setCauseImage(systemAttachmentService.clearPrefix(request.getCauseImage()));
            }
        }
        if(userLjhsOrder.getOrderType()==0){
            if(userLjhsOrder.getStatus() !=null && userLjhsOrder.getStatus()==31){
                userLjhsOrder.setCause(request.getCause());
                userLjhsOrder.setCauseImage(systemAttachmentService.clearPrefix(request.getCauseImage()));
            }
        }


        if(userLjhsOrder.getStatus() !=null && request.getStatus()==0){
            userLjhsOrder.setCname(request.getCname());
            userLjhsOrder.setCuid(request.getCuid());
            userLjhsOrder.setCphone(request.getCphone());
            userLjhsOrder.setStatus(1);
            userLjhsOrder.setCtime(new Date());
        }
        BeanUtils.copyProperties(userLjhsOrder,request);

        return updateLjdrOder(request);
    }

    @Override
    public UserLjhsOrder getByOderId(String orderId) {
        LambdaQueryWrapper<UserLjhsOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(UserLjhsOrder::getOrderId, orderId);
        return userLjhsOrderDao.selectOne(lqw);
    }

    @Override
    public Boolean createOthersOder(UserOthersOrdeRequest userOthersOrdeRequest) {
        UserOthersOrder userOthersOrder = new UserOthersOrder();
        BeanUtils.copyProperties(userOthersOrdeRequest, userOthersOrder);
//        userLjhsOrder.setOrderId(String.valueOf(System.currentTimeMillis()));
        //详情图
        userOthersOrder.setDetailImages(systemAttachmentService.clearPrefix(userOthersOrder.getDetailImages()));
        if(userOthersOrderDao.insert(userOthersOrder)>0){
            return true;
        }else{
            return false;
        }
    }


    /**
     * 订单列表
     * @param pageRequest 分页
     * @return CommonPage<OrderDetailResponse>
     */
    @Override
    public CommonPage<UserOthersOrderResponse> listOthersOrder(UserOthersOrdeRequest request, PageParamRequest pageRequest) {

        PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserOthersOrder> lqw = new LambdaQueryWrapper<>();
        if (request.getUid()!=null && request.getUid()!=0) {
            lqw.eq(UserOthersOrder::getUid, request.getUid());
        }else{
            throw new MallException("获取失败！");
        }
        lqw.eq(UserOthersOrder::getIsDel, 0);
        lqw.orderByDesc(UserOthersOrder::getId);
        List<UserOthersOrder> orderList = userOthersOrderDao.selectList(lqw);
        List<UserOthersOrderResponse> ljhsOrderResponses = new ArrayList<>();
        for(UserOthersOrder userOthersOrder :orderList){
            UserOthersOrderResponse userOthersOrderResponse = new UserOthersOrderResponse();
            BeanUtils.copyProperties(userOthersOrder, userOthersOrderResponse);
            User user = userService.getById(userOthersOrder.getUid());
            if(user !=null){
                userOthersOrderResponse.setUserName(user.getNickname());
            }
            ljhsOrderResponses.add(userOthersOrderResponse);
        }
        CommonPage<UserOthersOrderResponse> userOthersOrderResponseCommonPage = CommonPage.restPage(ljhsOrderResponses);
        return userOthersOrderResponseCommonPage;
    }

    /**
     * 订单列表
     * @param pageRequest 分页
     * @return CommonPage<OrderDetailResponse>
     */
    @Override
    public CommonPage<UserOthersOrderResponse> listAdminOthersOrder(UserOthersOrdeRequest request, PageParamRequest pageRequest) {

        PageHelper.startPage(pageRequest.getPage(), pageRequest.getLimit());
        LambdaQueryWrapper<UserOthersOrder> lqw = new LambdaQueryWrapper<>();
        if (request.getOrderType()!=null) {
            lqw.eq(UserOthersOrder::getOrderType, request.getOrderType());
        }
        if (StringUtils.isNotBlank(request.getDateLimit())) {
            dateLimitUtilVo dateLimitUtilVo = DateUtil.getDateLimit(request.getDateLimit());
            lqw.between(UserOthersOrder::getCreateTime, dateLimitUtilVo.getStartTime(), dateLimitUtilVo.getEndTime());
        }
        lqw.orderByDesc(UserOthersOrder::getId);
        List<UserOthersOrder> orderList = userOthersOrderDao.selectList(lqw);
        List<UserOthersOrderResponse> ljhsOrderResponses = new ArrayList<>();
        for(UserOthersOrder userOthersOrder :orderList){
            UserOthersOrderResponse userOthersOrderResponse = new UserOthersOrderResponse();
            BeanUtils.copyProperties(userOthersOrder, userOthersOrderResponse);
            User user = userService.getById(userOthersOrder.getUid());
            if(user !=null){
                userOthersOrderResponse.setUserName(user.getNickname());
            }
            ljhsOrderResponses.add(userOthersOrderResponse);
        }
        CommonPage<UserOthersOrderResponse> userOthersOrderResponseCommonPage = CommonPage.restPage(ljhsOrderResponses);
        return userOthersOrderResponseCommonPage;
    }


    @Override
    public UserOthersOrderResponse otherOrderDetailsByOrderId( Integer id) {

        LambdaQueryWrapper<UserOthersOrder> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserOthersOrder::getId, id);
        UserOthersOrder userOthersOrder= userOthersOrderDao.selectOne(lqw);

        UserOthersOrderResponse userOthersOrderResponse = new UserOthersOrderResponse();
        BeanUtils.copyProperties(userOthersOrder, userOthersOrderResponse);
        User user = userService.getById(userOthersOrderResponse.getUid()); //垃圾类型
        if(user !=null){
            userOthersOrderResponse.setUserName(user.getNickname());
        }
        return userOthersOrderResponse;
    }

    /**
     * 自动派送订单
     */
    @Override
    public Boolean autoSendOrder(UserLjhsOrder userLjhsOrder) {
        if(userLjhsOrder.getStatus() !=0){
            return true;
        }else{
            if(userLjhsOrder.getPid()==null || userLjhsOrder.getPid()== 0){
                throw new MallException("自动派送订单：参数错误！");
            }
            try {
                Category category = categoryService.getById(userLjhsOrder.getPid());
                if(category!=null){
                    if(category.getMyzl()!=null && !category.getMyzl().equals("")){
                        if(category.getMyzl().equals("0")){
                            throw new MallException("自动派送订单：派送点"+userLjhsOrder.getPid()+"未设置成自动派送！");
                        }
                    }else{
                        throw new MallException("自动派送订单：派送点"+userLjhsOrder.getPid()+"未设置成自动派送！");
                    }
                }else{
                    throw new MallException("自动派送订单：派送点"+userLjhsOrder.getPid()+"不存在！");
                }

                ServiceUser serviceUser = new ServiceUser();
                serviceUser.setType(userLjhsOrder.getOrderType());
                serviceUser.setCid(userLjhsOrder.getPid());
                List<ServiceUser>  list = userService.getListServiceUser(serviceUser);
                if(list !=null && list.size()>0){
                    Random r = new Random();
                    int num = r.nextInt(list.size());
                    User user = userService.getById(list.get(num).getUid());
                    if(user==null){
                        throw new MallException("自动派送订单：参数错误！");
                    }else{
                        userLjhsOrder.setCtime(new Date());
                        userLjhsOrder.setStatus(1);
                        userLjhsOrder.setCuid(user.getUid());
                        userLjhsOrder.setCname(user.getNickname());
                        userLjhsOrder.setCphone(user.getPhone());
                        UserLjhsOrdeRequest userLjhsOrdeRequest = new UserLjhsOrdeRequest();
                        BeanUtils.copyProperties(userLjhsOrder, userLjhsOrdeRequest);
                        return updateLjdrOder(userLjhsOrdeRequest);
                    }
                }else{
                    throw new MallException("服务点："+userLjhsOrder.getPid()+" 未配置人员");
                }
            }catch (Exception e){
                throw new MallException("订单自动派送异常："+e.getMessage());
            }
        }
    }

    /**
     * 服务订单7天自动评价
     */
    @Override
    public Boolean autoSendOrderReply(UserLjhsOrder userLjhsOrder) {
        if(userLjhsOrder.getStatus() !=6){
            return true;
        }else{
            try {
                UserLjhsOrderReply userLjhsOrderReply = new UserLjhsOrderReply();
                userLjhsOrderReply.setComment("服务非常棒！");
                userLjhsOrderReply.setQualityScore(5);
                userLjhsOrderReply.setServiceScore(5);
                userLjhsOrderReply.setOrderId(userLjhsOrder.getOrderId());
                if(!sericeOrderComment(userLjhsOrderReply)){
                    throw new MallException("服务订单自动评价失败");
                }else{
                    return true;
                }
            }catch (Exception e){
                throw new MallException("服务订单自动评价异常："+e.getMessage());
            }
        }
    }

    @Override
    public List<UserLjhsOrderStatus> listUserLjhsOrderStatus(String orderId) {
        LambdaQueryWrapper<UserLjhsOrderStatus> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserLjhsOrderStatus::getOrderId, orderId);
        lqw.orderByAsc(UserLjhsOrderStatus::getCreateTime);
        return userLjhsOrderStatusDao.selectList(lqw);
    }

    @Override
    public PageInfo<UserLjhsOrderReplyResponse> getListSericeOrderComment(Integer type, Integer typeId, PageParamRequest pageParamRequest){
        Page<UserLjhsOrderReply> userExtractPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserLjhsOrderReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserLjhsOrderReply::getType, typeId);

        switch (type){
            case 1:
                lqw.apply(" (quality_score + service_score) >= 8");
                break;
            case 2:
                lqw.apply(" (quality_score + service_score) < 8 and (quality_score + service_score) > 4");
                break;
            case 3:
                lqw.apply(" (quality_score + service_score) <= 4");
                break;
            default:
                break;
        }
        lqw.orderByDesc(UserLjhsOrderReply::getCreateTime);
        List<UserLjhsOrderReply> list = userLjhsOrderReplyDao.selectList(lqw);
        if(CollUtil.isEmpty(list)){
            return new PageInfo<>();
        }
        List<UserLjhsOrderReplyResponse> arrayList = new ArrayList<>();
        for(UserLjhsOrderReply userLjhsOrderReply:list){
            UserLjhsOrderReplyResponse userLjhsOrderReplyResponse = new UserLjhsOrderReplyResponse();
            BeanUtils.copyProperties(userLjhsOrderReply, userLjhsOrderReplyResponse);

            // 星数 = （商品评星 + 服务评星） / 2
            BigDecimal sumScore = new BigDecimal(userLjhsOrderReply.getQualityScore() + userLjhsOrderReply.getServiceScore());
            BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
            userLjhsOrderReplyResponse.setScore(divide.intValue());
            arrayList.add(userLjhsOrderReplyResponse);
        }
        return CommonPage.copyPageInfo(userExtractPage, arrayList);
    }


    @Override
    public StoreProductReplayCountResponse sericeOrderCommentCount(Integer type){

        MyRecord myRecord = getH5Count(type);
        StoreProductReplayCountResponse storeProductReplayCountResponse = new StoreProductReplayCountResponse();
        storeProductReplayCountResponse.setSumCount(myRecord.getLong("sumCount"));
        storeProductReplayCountResponse.setGoodCount(myRecord.getLong("goodCount"));
        storeProductReplayCountResponse.setInCount(myRecord.getLong("mediumCount"));
        storeProductReplayCountResponse.setPoorCount(myRecord.getLong("poorCount"));
        storeProductReplayCountResponse.setReplyChance(myRecord.getStr("replyChance"));
        storeProductReplayCountResponse.setReplyStar(myRecord.getInt("replyStar"));
        return  storeProductReplayCountResponse;
    }

    /**
     * H5商品评论统计
     * @return MyRecord
     */
    @Override
    public MyRecord getH5Count(Integer typeId) {
        // 评论总数
        Integer sumCount = getCountByScore(typeId, "0");
        // 好评总数
        Integer goodCount = getCountByScore(typeId, "1");
        // 中评总数
        Integer mediumCount = getCountByScore(typeId, "2");
        // 差评总数
        Integer poorCount = getCountByScore(typeId, "3");
        // 好评率
        String replyChance = "0";
        if(sumCount > 0 && goodCount > 0){
            replyChance = String.format("%.2f", ((goodCount.doubleValue() / sumCount.doubleValue())));
        }
        // 评分星数(商品评星 + 服务评星)/2
        Integer replyStar = 0;
        if (sumCount > 0) {
            replyStar = getSumStar(typeId);

        }
        MyRecord record = new MyRecord();
        record.set("sumCount", sumCount);
        record.set("goodCount", goodCount);
        record.set("mediumCount", mediumCount);
        record.set("poorCount", poorCount);
        record.set("replyChance", replyChance);
        record.set("replyStar", replyStar);
        return record;
    }

    /**
     * 商品分数
     * @return Integer
     */
    public Integer getSumStar(Integer typeId) {
        QueryWrapper<UserLjhsOrderReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("IFNULL(sum(quality_score),0) as quality_score", "IFNULL(sum(service_score),0) as service_score");
        queryWrapper.eq("is_del", 0);
        queryWrapper.eq("type", typeId);
        UserLjhsOrderReply userLjhsOrderReply = userLjhsOrderReplyDao.selectOne(queryWrapper);
        if (ObjectUtil.isNull(userLjhsOrderReply)){
            return 0;
        }
        if (userLjhsOrderReply.getQualityScore() == 0 || userLjhsOrderReply.getServiceScore() == 0) {
            return 0;
        }
        // 星数 = （商品评星 + 服务评星） / 2
        BigDecimal sumScore = new BigDecimal(userLjhsOrderReply.getQualityScore() + userLjhsOrderReply.getServiceScore());
        BigDecimal divide = sumScore.divide(BigDecimal.valueOf(2L), 0, BigDecimal.ROUND_DOWN);
        return divide.intValue();
    }

    // 获取统计数据（好评、中评、差评）
    private Integer getCountByScore(Integer typeId, String type) {

        LambdaQueryWrapper<UserLjhsOrderReply> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserLjhsOrderReply::getType, typeId);
        switch (type) {
            case "0":
                break;
            case "1":
                lqw.apply( " (quality_score + service_score) >= 8");
                break;
            case "2":
                lqw.apply( " (quality_score + service_score) < 8 and (quality_score + service_score) > 4");
                break;
            case "3":
                lqw.apply( " (quality_score + service_score) <= 4");
                break;
        }
        return userLjhsOrderReplyDao.selectCount(lqw);
    }
}

