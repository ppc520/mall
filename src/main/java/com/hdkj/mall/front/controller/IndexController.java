package com.hdkj.mall.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.hdkj.mall.front.response.IndexInfoResponse;
import com.hdkj.mall.front.response.IndexProductBannerResponse;
import com.hdkj.mall.front.response.IndexProductResponse;
import com.hdkj.mall.front.service.IndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

/**
 * 用户 -- 用户中心

 */
@Slf4j
@RestController("IndexController")
@RequestMapping("api/front")
@Api(tags = "首页")
public class IndexController {

    @Autowired
    private IndexService indexService;

    /**
     * 首页产品的轮播图和产品信息
     */
    @ApiOperation(value = "首页产品的轮播图和产品信息")
    @RequestMapping(value = "/groom/list/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】", dataType = "int", required = true)
    public CommonResult<IndexProductBannerResponse> getProductBanner(@PathVariable(value = "type") int type, PageParamRequest pageParamRequest){
        if(type < Constants.INDEX_RECOMMEND_BANNER || type > Constants.INDEX_BENEFIT_BANNER){
            return CommonResult.validateFailed();
        }
        return CommonResult.success(indexService.getProductBanner(type, pageParamRequest));
    }

    /**
     * 首页数据
     */
    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public CommonResult<IndexInfoResponse> getIndexInfo(){
        return CommonResult.success(indexService.getIndexInfo());
    }

    /**
     * 首页数据
     */
    @ApiOperation(value = "新首页数据")
    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public CommonResult<IndexInfoResponse> getHomeData(){
        return CommonResult.success(indexService.getHomeData());
    }

    /**
     * 服务首页数据
     */
    @ApiOperation(value = "服务首页数据")
    @RequestMapping(value = "/service/{type}", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Object>> getServiceData(@PathVariable(value = "type") Integer type){
        return CommonResult.success(indexService.getServiceData(type));
    }


    /**
     * 首页商品列表
     */
    @ApiOperation(value = "首页商品列表")
    @RequestMapping(value = "/index/product/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】", dataType = "int", required = true)
    public CommonResult<CommonPage<IndexProductResponse>> getProductBanner(@PathVariable(value = "type") Integer type, PageParamRequest pageParamRequest){
        if(type < Constants.INDEX_RECOMMEND_BANNER || type > Constants.INDEX_BENEFIT_BANNER){
            return CommonResult.validateFailed();
        }
        return CommonResult.success(indexService.findIndexProductList(type, pageParamRequest));
    }

    /**
     * 热门搜索
     */
    @ApiOperation(value = "热门搜索")
    @RequestMapping(value = "/search/keyword", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> hotKeywords(){
        return CommonResult.success(indexService.hotKeywords());
    }

    /**
     * 分享配置
     */
    @ApiOperation(value = "分享配置")
    @RequestMapping(value = "/share", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> share(){
        return CommonResult.success(indexService.getShareConfig());
    }

}



