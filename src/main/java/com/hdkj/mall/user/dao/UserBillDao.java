package com.hdkj.mall.user.dao;

import com.hdkj.mall.user.model.UserBill;
import com.hdkj.mall.user.response.UserBillResponse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * 用户账单表 Mapper 接口
 */
public interface UserBillDao extends BaseMapper<UserBill> {

    List<UserBillResponse> getListAdmin(Map<String, Object> map);

    List<UserBillResponse> getListAdminAndIntegeal(Map<String, Object> map);

    List<UserBillResponse> fundMonitoring(Map<String, Object> map);
}
