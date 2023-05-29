package com.hdkj.mall.system.service;

import com.common.PageParamRequest;
import com.hdkj.mall.system.model.SystemGroupData;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.system.request.SystemGroupDataRequest;
import com.hdkj.mall.system.request.SystemGroupDataSearchRequest;

import java.util.HashMap;
import java.util.List;

/**
 * SystemGroupDataService 接口
 */
public interface SystemGroupDataService extends IService<SystemGroupData> {

    List<SystemGroupData> getList(SystemGroupDataSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(SystemGroupDataRequest systemGroupDataRequest);

    boolean update(Integer id, SystemGroupDataRequest request);

    <T> List<T> getListByGid(Integer gid, Class<T> cls);

    List<HashMap<String, Object>> getListMapByGid(Integer gid);

    <T> T getNormalInfo(Integer groupDataId, Class<T> cls);

    /**
     * 获取个人中心菜单
     * @return HashMap<String, Object>
     */
    HashMap<String, Object> getMenuUser();
}