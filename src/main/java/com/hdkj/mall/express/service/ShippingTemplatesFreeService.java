package com.hdkj.mall.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.express.model.ShippingTemplatesFree;
import com.hdkj.mall.express.request.ShippingTemplatesFreeRequest;

import java.util.List;

/**
* ShippingTemplatesFreeService 接口
*/
public interface ShippingTemplatesFreeService extends IService<ShippingTemplatesFree> {

    List<ShippingTemplatesFree> getList(PageParamRequest pageParamRequest);

    void saveAll(List<ShippingTemplatesFreeRequest> shippingTemplatesFreeRequestList, Integer type, Integer id);

    List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId);

    void delete(Integer id);

    /**
     * 根据模板编号、城市ID查询
     * @param tempId 模板编号
     * @param cityId 城市ID
     * @return 运费模板
     */
    ShippingTemplatesFree getByTempIdAndCityId(Integer tempId, Integer cityId);
}
