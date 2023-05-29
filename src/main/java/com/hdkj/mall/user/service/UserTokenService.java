package com.hdkj.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.user.model.UserToken;

import java.util.List;

/**
 * UserTokenService 接口实现
 */
public interface UserTokenService extends IService<UserToken> {

    UserToken getByOpenidAndType(String token, int type);

    void bind(String openId, int type, Integer uid);

    UserToken getTokenByUserId(Integer userId, int type);

    List<UserToken> getList(List<Integer> userIdList);

    UserToken getByUid(Integer uid);
}