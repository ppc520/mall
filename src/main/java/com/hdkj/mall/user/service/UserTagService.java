package com.hdkj.mall.user.service;

import com.common.PageParamRequest;
import com.hdkj.mall.user.model.UserTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * UserTagService 接口实现
 */
public interface UserTagService extends IService<UserTag> {

    List<UserTag> getList(PageParamRequest pageParamRequest);

    String getGroupNameInId(String tagIdValue);
}