package com.hdkj.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.front.response.UserSignInfoResponse;
import com.hdkj.mall.system.vo.SystemGroupDataSignConfigVo;
import com.hdkj.mall.user.model.UserSign;
import com.hdkj.mall.user.vo.UserSignVo;
import com.hdkj.mall.user.vo.UserSignMonthVo;

import java.util.HashMap;
import java.util.List;

/**
 * UserSignService 接口实现
 */
public interface UserSignService extends IService<UserSign> {

    List<UserSignVo> getList(PageParamRequest pageParamRequest);

    List<UserSign> getListByCondition(UserSign sign,PageParamRequest pageParamRequest);

    SystemGroupDataSignConfigVo sign();

    HashMap<String, Object> get();

    List<SystemGroupDataSignConfigVo> config();

    List<UserSignMonthVo> getListGroupMonth(PageParamRequest pageParamRequest);

    /**
     * 获取用户签到信息
     * @return UserSignInfoResponse
     */
    UserSignInfoResponse getUserSignInfo();
}