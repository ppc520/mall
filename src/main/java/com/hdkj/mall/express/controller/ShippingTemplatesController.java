package com.hdkj.mall.express.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.hdkj.mall.express.model.ShippingTemplates;
import com.hdkj.mall.express.request.ShippingTemplatesRequest;
import com.hdkj.mall.express.request.ShippingTemplatesSearchRequest;
import com.hdkj.mall.express.service.ShippingTemplatesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

/**
 * 物流-模板控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/express/shipping/templates")
@Api(tags = "设置 -- 物流 -- 模板")
public class ShippingTemplatesController {

    @Autowired
    private ShippingTemplatesService shippingTemplatesService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-04-17
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ShippingTemplates>>  getList(@Validated ShippingTemplatesSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<ShippingTemplates> shippingTemplatesCommonPage = CommonPage.restPage(shippingTemplatesService.getList(request, pageParamRequest));
        return CommonResult.success(shippingTemplatesCommonPage);
    }

    /**
     * 新增
     * @param request 新增参数
     * @author Mr.Zhou
     * @since 2022-04-17
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated ShippingTemplatesRequest request){
        if (shippingTemplatesService.create(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed("新增运费模板失败");
    }

    /**
     * 删除
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-17
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(shippingTemplatesService.remove(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改
     * @param id integer id
     * @param request ShippingTemplatesRequest 修改参数
     * @author Mr.Zhou
     * @since 2022-04-17
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated ShippingTemplatesRequest request){
        shippingTemplatesService.update(id, request);
        return CommonResult.success();
    }

    /**
     * 查询信息
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-17
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<ShippingTemplates> info(@RequestParam(value = "id") Integer id){
        ShippingTemplates shippingTemplates = shippingTemplatesService.getById(id);
        return CommonResult.success(shippingTemplates);
   }
}



