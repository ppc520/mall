package com.hdkj.mall.front.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.finance.request.UserExtractRequest;
import com.hdkj.mall.front.request.WxBindingPhoneRequest;
import com.hdkj.mall.front.response.*;
import com.hdkj.mall.system.model.SystemUserLevel;
import com.hdkj.mall.user.model.UserBill;
import com.hdkj.mall.user.model.UserIntegralRecord;
import com.hdkj.mall.front.request.UserRechargeRequest;
import com.hdkj.mall.front.request.UserSpreadPeopleRequest;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.request.RegisterThirdUserRequest;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户中心 服务类

 */
public interface UserCenterService extends IService<User> {

    UserRechargeResponse getTranferConfig();

    UserCommissionResponse getCommission();

    BigDecimal getSpreadCountByType(int type);

    Boolean extractCash(UserExtractRequest request);

    /**
     * 获取提现银行列表
     * @return List<String>
     */
    List<String> getExtractBank();

    List<SystemUserLevel> getUserLevelList();

    /**
     * 获取推广人列表
     * @param request 查询参数
     * @param pageParamRequest 分页
     * @return List<UserSpreadPeopleItemResponse>
     */
    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(UserSpreadPeopleRequest request, PageParamRequest pageParamRequest);

    UserRechargeResponse getRechargeConfig();

    UserBalanceResponse getUserBalance();

    UserSpreadOrderResponse getSpreadOrder(PageParamRequest pageParamRequest);

    OrderPayResultResponse recharge(UserRechargeRequest request);

    LoginResponse weChatAuthorizeLogin(String code, Integer spreadUid);

    String getLogo();

    LoginResponse weChatAuthorizeProgramLogin(String code, RegisterThirdUserRequest request);

    List<User> getTopSpreadPeopleListByDate(String type, PageParamRequest pageParamRequest);

    List<User> getTopBrokerageListByDate(String type, PageParamRequest pageParamRequest);

    List<UserSpreadBannerResponse> getSpreadBannerList(PageParamRequest pageParamRequest);

    Integer getNumberByTop(String type);

    Boolean transferIn(BigDecimal price);

    PageInfo<UserExtractRecordResponse> getExtractRecord(PageParamRequest pageParamRequest);

    /**
     * 推广佣金明细
     * @param pageParamRequest 分页参数
     */
    PageInfo<SpreadCommissionDetailResponse> getSpreadCommissionDetail(PageParamRequest pageParamRequest);

    /**
     * 用户账单记录（现金）
     * @param type 记录类型：all-全部，expenditure-支出，income-收入
     * @return CommonPage
     */
    CommonPage<UserRechargeBillRecordResponse> nowMoneyBillRecord(String type, PageParamRequest pageRequest);

    /**
     * 微信注册绑定手机号
     * @param request 请求参数
     * @return 登录信息
     */
    LoginResponse registerBindingPhone(WxBindingPhoneRequest request);

    /**
     * 用户积分记录列表
     * @param pageParamRequest 分页参数
     * @return List<UserIntegralRecord>
     */
    List<UserIntegralRecord> getUserIntegralRecordList(PageParamRequest pageParamRequest);

    /**
     * 获取用户积分信息
     * @return IntegralUserResponse
     */
    IntegralUserResponse getIntegralUser();

    /**
     * 获取用户经验记录
     * @param pageParamRequest 分页参数
     * @return List<UserBill>
     */
    List<UserBill> getUserExperienceList(PageParamRequest pageParamRequest);

    /**
     * 提现用户信息
     * @return UserExtractCashResponse
     */
    UserExtractCashResponse getExtractUser();

    /**
     * 推广人列表统计
     * @return UserSpreadPeopleResponse
     */
    UserSpreadPeopleResponse getSpreadPeopleCount();
}
