package com.hdkj.mall.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdkj.mall.front.response.UserSpreadPeopleItemResponse;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.vo.UserOperateFundsVo;

import java.util.List;
import java.util.Map;

/**
 * 用户表 Mapper 接口
 */
public interface UserDao extends BaseMapper<User> {
    Boolean updateFounds(UserOperateFundsVo userOperateFundsVo);

    List<UserSpreadPeopleItemResponse> getSpreadPeopleList(Map<String, Object> map);

    List<User> findAdminList(Map<String, Object> map);
}
