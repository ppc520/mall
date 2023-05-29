package com.hdkj.mall.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.MyRecord;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.front.request.UserCouponReceiveRequest;
import com.hdkj.mall.marketing.model.StoreCouponUser;
import com.hdkj.mall.marketing.request.StoreCouponUserRequest;
import com.hdkj.mall.marketing.request.StoreCouponUserSearchRequest;
import com.hdkj.mall.marketing.response.StoreCouponUserOrder;
import com.hdkj.mall.marketing.response.StoreCouponUserResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * StoreCouponUserService 接口
 */
public interface StoreCouponUserService extends IService<StoreCouponUser> {

    PageInfo<StoreCouponUserResponse> getList(StoreCouponUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 基本条件查询
     * @param storeCouponUser 基本参数
     * @return 查询的优惠券结果
     */
    List<StoreCouponUser> getList(StoreCouponUser storeCouponUser);

    Boolean receive(StoreCouponUserRequest storeCouponUserRequest);

    /**
     * 检测优惠券是否可用，计算订单价格时使用
     * @param id            优惠券id
     * @param productIdList 商品id集合
     * @param price 价格
     * @return  可用状态
     */
    boolean canUse(Integer id, List<Integer> productIdList, BigDecimal price);

    HashMap<Integer, StoreCouponUser> getMapByUserId(Integer userId);

    /**
     * 根据购物车id获取可用优惠券
     * @param preOrderNo 预下单订单号
     * @return 可用优惠券集合
     */
    List<StoreCouponUserOrder> getListByPreOrderNo(String preOrderNo);

    /**
     * 优惠券过期定时任务
     */
    void overdueTask();

    /**
     * 用户领取优惠券
     */
    Boolean receiveCoupon(UserCouponReceiveRequest request);

    /**
     * 支付成功赠送处理
     * @param couponId 优惠券编号
     * @param uid  用户uid
     * @return MyRecord
     */
    MyRecord paySuccessGiveAway(Integer couponId, Integer uid);

    /**
     * 根据uid获取列表
     * @param uid uid
     * @param pageParamRequest 分页参数
     * @return List<StoreCouponUser>
     */
    List<StoreCouponUser> findListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取可用优惠券数量
     * @param uid 用户uid
     */
    Integer getUseCount(Integer uid);

    /**
     * 我的优惠券列表
     * @param type 类型，usable-可用，unusable-不可用
     * @param pageParamRequest 分页参数
     * @return CommonPage<StoreCouponUserResponse>
     */
    CommonPage<StoreCouponUserResponse> getMyCouponList(String type, PageParamRequest pageParamRequest);
}
