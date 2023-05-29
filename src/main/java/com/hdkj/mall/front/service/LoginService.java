package com.hdkj.mall.front.service;

import com.hdkj.mall.front.request.LoginMobileRequest;
import com.hdkj.mall.front.request.LoginRequest;
import com.hdkj.mall.front.response.LoginResponse;
import com.hdkj.mall.user.model.User;

/**
 * 移动端登录服务类
 */
public interface LoginService {

    /**
     * 账号密码登录
     * @return LoginResponse
     */
    LoginResponse login(LoginRequest loginRequest) throws Exception;

    /**
     * 手机号验证码登录
     */
    LoginResponse phoneLogin(LoginMobileRequest loginRequest) throws Exception;

    /**
     * 老绑定分销关系
     * @param user User 用户user类
     * @param spreadUid Integer 推广人id
     * @return Boolean
     */
    Boolean bindSpread(User user, Integer spreadUid);
}
