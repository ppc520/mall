package com.hdkj.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.system.model.SystemUserLevel;
import com.hdkj.mall.system.request.SystemUserLevelRequest;
import com.hdkj.mall.system.request.SystemUserLevelSearchRequest;

import java.util.List;

/**
 * SystemUserLevelService 接口
 */
public interface SystemUserLevelService extends IService<SystemUserLevel> {

    List<SystemUserLevel> getList(SystemUserLevelSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemUserLevelRequest request);

    boolean update(Integer id, SystemUserLevelRequest request);

    SystemUserLevel getByLevelId(Integer levelId);

    /**
     * 获取系统等级列表（移动端）
     */
    List<SystemUserLevel> getH5LevelList();
}
