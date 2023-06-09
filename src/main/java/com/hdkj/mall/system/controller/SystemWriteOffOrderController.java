package com.hdkj.mall.system.controller;

import com.common.CommonResult;
import com.common.PageParamRequest;
import com.hdkj.mall.store.service.StoreOrderService;
import com.hdkj.mall.system.request.SystemWriteOffOrderSearchRequest;
import com.hdkj.mall.system.response.SystemWriteOffOrderResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 订单表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/system/store/order")
@Api(tags = "设置 -- 提货点 -- 核销订单") //配合swagger使用
public class SystemWriteOffOrderController {

    @Autowired
    private StoreOrderService storeOrderService;

    /**
     * 分页显示订单表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-05-28
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public CommonResult<SystemWriteOffOrderResponse>  getList(
            @Validated SystemWriteOffOrderSearchRequest request,
            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(storeOrderService.getWriteOffList(request, pageParamRequest));
    }
}



