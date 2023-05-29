package com.hdkj.mall.finance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.finance.model.UserFundsMonitor;
import com.hdkj.mall.finance.request.FundsMonitorUserSearchRequest;
import com.hdkj.mall.user.model.UserBrokerageRecord;

import java.util.List;

/**
*  UserRechargeService 接口
*/
public interface UserFundsMonitorService extends IService<UserFundsMonitor> {

    /**
     * 佣金列表
     * @author Mr.Zhou
     * @since 2022-04-28
     * @return List<User>
     */
    List<UserFundsMonitor> getFundsMonitor(FundsMonitorUserSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 佣金详细记录
     * @param uid 用户uid
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return
     */
    PageInfo<UserBrokerageRecord> getFundsMonitorDetail(Integer uid, String dateLimit, PageParamRequest pageParamRequest);
}
