package com.hdkj.mall.finance.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdkj.mall.finance.model.UserFundsMonitor;

import java.util.HashMap;
import java.util.List;

/**
 * 用户充值表 Mapper 接口

 */
public interface UserFundsMonitorDao extends BaseMapper<UserFundsMonitor> {

    /**
     * 佣金列表
     * @author Mr.Zhou
     * @since 2022-04-28
     * @return List<User>
     */
    List<UserFundsMonitor> getFundsMonitor(HashMap<String, Object> map);
}
