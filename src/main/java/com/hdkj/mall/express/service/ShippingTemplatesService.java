package com.hdkj.mall.express.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.express.model.ShippingTemplates;
import com.hdkj.mall.express.request.ShippingTemplatesRequest;
import com.hdkj.mall.express.request.ShippingTemplatesSearchRequest;

import java.util.List;

/**
* ShippingTemplatesService 接口
*/
public interface ShippingTemplatesService extends IService<ShippingTemplates> {

    List<ShippingTemplates> getList(ShippingTemplatesSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 新增运费模板
     * @param request 请求参数
     * @return 新增结果
     */
    Boolean create(ShippingTemplatesRequest request);

    boolean update(Integer id, ShippingTemplatesRequest request);

    boolean remove(Integer id);

}
