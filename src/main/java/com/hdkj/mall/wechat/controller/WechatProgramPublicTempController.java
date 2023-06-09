package com.hdkj.mall.wechat.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.hdkj.mall.wechat.request.WechatProgramPublicTempSearchRequest;
import com.hdkj.mall.wechat.model.WechatProgramPublicTemp;
import com.hdkj.mall.wechat.service.WechatProgramPublicTempService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 *  微信公共模板库
 */
@Slf4j
@RestController
@RequestMapping("api/admin/wechat/program/public/temp")
@Api(tags = "微信开放平台 -- 小程序 -- 微信公共模板库") //配合swagger使用

public class WechatProgramPublicTempController {

    @Autowired
    private WechatProgramPublicTempService wechatProgramPublicTempService;

    /**
     * 分页显示
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-08-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<WechatProgramPublicTemp>> getList(@Validated WechatProgramPublicTempSearchRequest request, @Validated PageParamRequest pageParamRequest){
        CommonPage<WechatProgramPublicTemp> wechatProgramPublicTempCommonPage = CommonPage.restPage(wechatProgramPublicTempService.getList(request, pageParamRequest));
        return CommonResult.success(wechatProgramPublicTempCommonPage);
    }

    /**
     * 查询信息
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-08-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<WechatProgramPublicTemp> info(@RequestParam(value = "id") Integer id){
        WechatProgramPublicTemp wechatProgramPublicTemp = wechatProgramPublicTempService.getById(id);
        return CommonResult.success(wechatProgramPublicTemp);
   }
}



