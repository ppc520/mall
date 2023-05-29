package com.hdkj.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.system.model.SystemCity;
import com.hdkj.mall.system.request.SystemCitySearchRequest;
import com.hdkj.mall.system.request.SystemCityRequest;

import java.util.List;

/**
 * SystemCityService 接口
 */
public interface SystemCityService extends IService<SystemCity> {

    Object getList(SystemCitySearchRequest request);

    boolean updateStatus(Integer id, Boolean status);

    boolean update(Integer id, SystemCityRequest request);

    Object getListTree();

    List<Integer> getCityIdList();

    SystemCity getCityByCityId(Integer cityId);
    /**
     * 根据城市名称获取城市详细数据
     * @param cityName 城市名称
     * @return 城市数据
     */
    SystemCity getCityByCityName(String cityName);
}
