package com.hdkj.mall.system.dao;

import com.hdkj.mall.system.model.SystemStore;
import com.hdkj.mall.front.request.StoreNearRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdkj.mall.system.vo.SystemStoreNearVo;

import java.util.List;

/**
 * 门店自提 Mapper 接口
 */
public interface SystemStoreDao extends BaseMapper<SystemStore> {

    List<SystemStoreNearVo> getNearList(StoreNearRequest request);
}

