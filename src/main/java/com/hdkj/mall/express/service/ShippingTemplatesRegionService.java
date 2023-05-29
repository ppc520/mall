package com.hdkj.mall.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.express.model.ShippingTemplatesRegion;
import com.hdkj.mall.express.request.ShippingTemplatesRegionRequest;

import java.util.List;

/**
* ShippingTemplatesRegionService 接口
*/
public interface ShippingTemplatesRegionService extends IService<ShippingTemplatesRegion> {

    List<ShippingTemplatesRegion> getList(PageParamRequest pageParamRequest);

    void saveAll(List<ShippingTemplatesRegionRequest> shippingTemplatesRegionRequestList, Integer type, Integer id);

    List<ShippingTemplatesRegionRequest> getListGroup(Integer tempId);

    void delete(Integer tempId);

    /**
     * 根据模板编号、城市ID查询
     * @param tempId 模板编号
     * @param cityId 城市ID
     * @return 运费模板
     */
    ShippingTemplatesRegion getByTempIdAndCityId(Integer tempId, Integer cityId);
}
