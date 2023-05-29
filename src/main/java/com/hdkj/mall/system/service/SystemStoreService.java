package com.hdkj.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.system.model.SystemStore;
import com.hdkj.mall.front.request.StoreNearRequest;
import com.hdkj.mall.front.response.StoreNearResponse;
import com.hdkj.mall.system.request.SystemStoreRequest;

import java.util.HashMap;
import java.util.List;

/**
 * SystemStoreService 接口
 */
public interface SystemStoreService extends IService<SystemStore> {

    List<SystemStore> getList(String keywords, int status, PageParamRequest pageParamRequest);

    /**
     * 根据基本参数获取
     * @param systemStore 基本参数
     * @return 门店自提结果
     */
    SystemStore getByCondition(SystemStore systemStore);

    Boolean updateStatus(Integer id, boolean status);

    Boolean delete(Integer id);

    HashMap<String, Integer> getCount();

    HashMap<Integer, SystemStore> getMapInId(List<Integer> storeIdList);

    StoreNearResponse getNearList(StoreNearRequest request, PageParamRequest pageParamRequest);

    Boolean create(SystemStoreRequest request);

    Boolean update(Integer id, SystemStoreRequest request);

    /**
     * 彻底删除
     * @param id 提货点编号
     */
    Boolean completeLyDelete(Integer id);

    /**
     * 提货点恢复
     * @param id 提货点编号
     */
    Boolean recovery(Integer id);
}