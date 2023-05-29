package com.hdkj.mall.user.service;

import com.common.PageParamRequest;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.model.UserLevel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.user.request.UserLevelSearchRequest;

import java.util.List;

/**
 * UserLevelService 接口实现
 */
public interface UserLevelService extends IService<UserLevel> {

    List<UserLevel> getList(UserLevelSearchRequest request, PageParamRequest pageParamRequest);

    boolean level(Integer userId, int levelId);

    /**
     * 根据用户id获取用户等级
     * @param userId 用户id
     * @return 用户等级
     */
    UserLevel getUserLevelByUserId(Integer userId);

    /**
     * 经验升级
     * @param user
     * @return
     */
    Boolean upLevel(User user);
}