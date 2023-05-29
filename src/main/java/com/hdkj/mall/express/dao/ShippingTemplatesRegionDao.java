package com.hdkj.mall.express.dao;

import com.hdkj.mall.express.model.ShippingTemplatesRegion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdkj.mall.express.request.ShippingTemplatesRegionRequest;

import java.util.List;

/**
 *  Mapper 接口

 */
public interface ShippingTemplatesRegionDao extends BaseMapper<ShippingTemplatesRegion> {

    List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId);
}
