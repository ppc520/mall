package com.hdkj.mall.express.dao;

import com.hdkj.mall.express.model.ShippingTemplatesFree;
import com.hdkj.mall.express.request.ShippingTemplatesFreeRequest;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 *  Mapper 接口

 */
public interface ShippingTemplatesFreeDao extends BaseMapper<ShippingTemplatesFree> {

    List<ShippingTemplatesFreeRequest> getListGroup(Integer tempId);
}
