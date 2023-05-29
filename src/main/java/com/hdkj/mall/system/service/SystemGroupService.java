package com.hdkj.mall.system.service;

import com.common.PageParamRequest;
import com.hdkj.mall.system.model.SystemGroup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.system.request.SystemGroupSearchRequest;

import java.util.List;

/**
 * SystemGroupService 接口
 */
public interface SystemGroupService extends IService<SystemGroup> {

    List<SystemGroup> getList(SystemGroupSearchRequest request, PageParamRequest pageParamRequest);
}
