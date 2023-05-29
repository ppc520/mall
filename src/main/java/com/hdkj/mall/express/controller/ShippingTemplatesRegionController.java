package com.hdkj.mall.express.controller;

import com.common.CommonResult;
import com.hdkj.mall.express.request.ShippingTemplatesRegionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.hdkj.mall.express.service.ShippingTemplatesRegionService;

import java.util.List;


/**
 *  物流付费前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/express/shipping/region")
@Api(tags = "设置 -- 物流 -- 付费")
public class ShippingTemplatesRegionController {

    @Autowired
    private ShippingTemplatesRegionService shippingTemplatesRegionService;

    /**
     * 根据模板id查询数据
     * @param tempId Integer 模板id
     * @author Mr.Zhou
     * @since 2022-04-17
     */
    @ApiOperation(value = "根据模板id查询数据")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<List<ShippingTemplatesRegionRequest>> getList(@RequestParam Integer tempId){
        return CommonResult.success(shippingTemplatesRegionService.getListGroup(tempId));
    }
}



