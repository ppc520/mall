package com.hdkj.mall.finance.service;

import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.finance.model.UserRecharge;
import com.hdkj.mall.finance.request.UserRechargeRefundRequest;
import com.hdkj.mall.finance.request.UserRechargeSearchRequest;
import com.hdkj.mall.finance.response.UserRechargeResponse;
import com.hdkj.mall.front.request.UserRechargeRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.HashMap;

/**
* UserRechargeService 接口
*/
public interface UserRechargeService extends IService<UserRecharge> {

    PageInfo<UserRechargeResponse> getList(UserRechargeSearchRequest request, PageParamRequest pageParamRequest);

    HashMap<String, BigDecimal> getBalanceList();

    UserRecharge getInfoByEntity(UserRecharge userRecharge);

    UserRecharge create(UserRechargeRequest request);

    Boolean complete(UserRecharge userRecharge);

    /**
     * 充值退款
     * @param request 退款参数
     * @return Boolean
     */
    Boolean refund(UserRechargeRefundRequest request);

    /**
     * 获取用户累计充值金额
     * @param uid 用户uid
     * @return BigDecimal
     */
    BigDecimal getTotalRechargePrice(Integer uid);
}
