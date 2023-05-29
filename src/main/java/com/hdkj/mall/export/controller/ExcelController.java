package com.hdkj.mall.export.controller;

import cn.hutool.core.collection.CollUtil;
import com.common.CommonResult;
import com.hdkj.mall.article.model.ActivitySign;
import com.hdkj.mall.bargain.request.StoreBargainSearchRequest;
import com.hdkj.mall.store.request.StoreProductSearchRequest;
import com.hdkj.mall.combination.request.StoreCombinationSearchRequest;
import com.hdkj.mall.export.service.ExcelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;


/**
 * Excel导出 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/export/excel")
@Api(tags = "导出 -- Excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    /**
     * 商品导出
     */
    @ApiOperation(value = "产品")
    @RequestMapping(value = "/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> export(@Validated StoreProductSearchRequest request, HttpServletResponse response){
//        List<ProductExcelVo> productExcelVoList = excelService.product(request, response);
//        ExcelUtil.setSheetName("store");   //sheet名称
//        ExcelUtil.setFileName("产品导出");  //文件名称前缀  xx_yyyymmddhhiiss
//        ExcelUtil.writeExcel(response, productExcelVoList, ProductExcelVo.class);
        String fileName = excelService.exportProduct(request, response);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 砍价商品导出
     * @return
     */
    @ApiOperation(value = "砍价商品导出")
    @RequestMapping(value = "/bargain/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportBargainProduct(@Validated StoreBargainSearchRequest request, HttpServletResponse response){
        String fileName = excelService.exportBargainProduct(request, response);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 拼团商品导出
     * @return
     */
    @ApiOperation(value = "拼团商品导出")
    @RequestMapping(value = "/combiantion/product", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportCombinationProduct(@Validated StoreCombinationSearchRequest request, HttpServletResponse response){
        String fileName = excelService.exportCombinationProduct(request, response);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

    /**
     * 活动报名导出
     * @return
     */
    @ApiOperation(value = "活动报名导出")
    @RequestMapping(value = "/activity/user", method = RequestMethod.GET)
    public CommonResult<HashMap<String, String>> exportActivityUser(@Validated ActivitySign activitySign, HttpServletResponse response){
        String fileName = excelService.exportActivityUser(activitySign, response);
        HashMap<String, String> map = CollUtil.newHashMap();
        map.put("fileName", fileName);
        return CommonResult.success(map);
    }

}



