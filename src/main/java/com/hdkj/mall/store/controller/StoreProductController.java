package com.hdkj.mall.store.controller;

import com.common.CheckAdminToken;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.hdkj.mall.store.request.StoreCopyProductRequest;
import com.hdkj.mall.store.request.StoreProductSearchRequest;
import com.hdkj.mall.store.request.StoreProductStockRequest;
import com.hdkj.mall.store.response.StoreProductTabsHeader;
import com.hdkj.mall.store.service.StoreProductService;
import com.hdkj.mall.system.response.SystemAdminResponse;
import com.hdkj.mall.system.service.SystemAdminService;
import com.hdkj.mall.store.model.StoreProduct;
import com.hdkj.mall.store.request.StoreProductRequest;
import com.hdkj.mall.store.response.StoreProductResponse;
import com.hdkj.mall.store.service.StoreCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * 商品表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/product")
@Api(tags = "商品") //配合swagger使用
public class StoreProductController {

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private StoreCartService storeCartService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private CheckAdminToken checkAdminToken;


    /**
     * 分页显示商品表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-05-27
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreProductResponse>> getList(
            @Validated StoreProductSearchRequest storeProductSearchRequest,
            @Validated PageParamRequest pageParamRequest,
            HttpServletRequest request){
        try {
            String token = checkAdminToken.getTokenFormRequest(request);
            SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);
            if(systemAdminResponse!=null){
                if(systemAdminResponse.getRoles().equals("7")){
                    storeProductSearchRequest.setMerId(systemAdminResponse.getId());
                }
            }
        }catch (Exception e){}

        return CommonResult.success(CommonPage.restPage(storeProductService.getList(storeProductSearchRequest, pageParamRequest)));
    }

    /**
     * 新增商品表
     * @param storeProductRequest 新增参数
     * @author Mr.Zhou
     * @since 2022-05-27
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@RequestBody @Validated StoreProductRequest storeProductRequest){
        if(storeProductService.save(storeProductRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除商品表
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-05-27
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestBody @PathVariable Integer id, @RequestParam(value = "type", required = false, defaultValue = "recycle")String type){
        if(storeProductService.deleteProduct(id, type)){
            if (type.equals("recycle")) {
                storeCartService.productStatusNotEnable(id);
            } else {
                storeCartService.productDelete(id);
            }
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 恢复已删除商品表
     * @param id Integer
     * @author Stivepeim
     * @since 2022-08-28
     */
    @ApiOperation(value = "恢复商品")
    @RequestMapping(value = "/restore/{id}", method = RequestMethod.GET)
    public CommonResult<String> restore(@RequestBody @PathVariable Integer id){
        if(storeProductService.reStoreProduct(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改商品表
     * @param storeProductRequest 修改参数
     * @author Mr.Zhou
     * @since 2022-05-27
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreProductRequest storeProductRequest){
        if(storeProductService.update(storeProductRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询商品表信息
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-05-27
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info/{id}", method = RequestMethod.GET)
    public CommonResult<StoreProductResponse> info(@PathVariable Integer id){
        StoreProductResponse storeProductResponse = storeProductService.getByProductId(id);
        return CommonResult.success(storeProductResponse);
   }

    /**
     * 商品tabs表头数据
     * @return
     */
   @ApiOperation(value = "商品表头数量")
   @RequestMapping(value = "/tabs/headers", method = RequestMethod.GET)
   public CommonResult<List<StoreProductTabsHeader>> getTabsHeader(HttpServletRequest request){
       StoreProductSearchRequest storeProductSearchRequest = new StoreProductSearchRequest();
       try {
           String token = checkAdminToken.getTokenFormRequest(request);
           SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);
           if(systemAdminResponse!=null){
               if(systemAdminResponse.getRoles().equals("7")){
                   storeProductSearchRequest.setMerId(systemAdminResponse.getId());
               }
           }
       }catch (Exception e){}
        return CommonResult.success(storeProductService.getTabsHeader(storeProductSearchRequest));
   }


    /**
     * 服务商品tabs表头数据
     * @return
     */
    @ApiOperation(value = "服务商品tabs表头数据")
    @RequestMapping(value = "/tabs/fwheaders/{type}", method = RequestMethod.GET)
    public CommonResult<List<StoreProductTabsHeader>> getTabsFwHeader(HttpServletRequest request,@PathVariable Integer type){
        StoreProductSearchRequest storeProductSearchRequest = new StoreProductSearchRequest();
        try {
            String token = checkAdminToken.getTokenFormRequest(request);
            SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);
            if(systemAdminResponse!=null){
                if(systemAdminResponse.getRoles().equals("7")){
                    storeProductSearchRequest.setMerId(systemAdminResponse.getId());
                }
            }
        }catch (Exception e){}
        return CommonResult.success(storeProductService.getTabsFwHeader(storeProductSearchRequest,type));
    }


    /**
     * 上架
     */
    @ApiOperation(value = "上架")
    @RequestMapping(value = "/putOnShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> putOn(@PathVariable Integer id){
        if(storeProductService.putOnShelf(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 下架
     */
    @ApiOperation(value = "下架")
    @RequestMapping(value = "/offShell/{id}", method = RequestMethod.GET)
    public CommonResult<String> offShell(@PathVariable Integer id){
        if(storeProductService.offShelf(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 虚拟销量
     * @param id integer id
     * @author Mr.Zhou
     * @since 2022-05-06
     */
    @ApiOperation(value = "虚拟销量")
    @RequestMapping(value = "/ficti/{id}/{num}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", dataType = "int",  required = true),
            @ApiImplicitParam(name = "num", value = "数值", dataType = "int", required = true),
    })
    public CommonResult<String> sale(@PathVariable Integer id, @PathVariable Integer num){
        StoreProduct storeProduct = storeProductService.getById(id);
        storeProduct.setFicti(num);
        if(storeProductService.updateById(storeProduct)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 库存变动
     * @param request StoreProductStockRequest 参数
     * @author Mr.Zhou
     * @since 2022-05-19
     */
    @ApiOperation(value = "库存变动")
    @RequestMapping(value = "/stock", method = RequestMethod.GET)
    public CommonResult<String> stock(@Validated StoreProductStockRequest request){
        if(storeProductService.stockAddRedis(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    @ApiOperation(value = "导入99Api商品")
    @RequestMapping(value = "/importProduct", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "form", value = "导入平台1=淘宝，2=京东，3=苏宁，4=拼多多, 5=天猫", dataType = "int",  required = true),
            @ApiImplicitParam(name = "url", value = "URL", dataType = "String", required = true),
    })
    public CommonResult<StoreProductRequest> importProduct(
            @RequestParam @Valid int form,
            @RequestParam @Valid String url) throws IOException, JSONException {
        StoreProductRequest productRequest = storeProductService.importProductFromUrl(url, form);
        return CommonResult.success(productRequest);
    }

    /**
     * 获取复制商品配置
     */
    @ApiOperation(value = "获取复制商品配置")
    @RequestMapping(value = "/copy/config", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyConfig() {
        return CommonResult.success(storeProductService.copyConfig());
    }

    @ApiOperation(value = "复制平台商品")
    @RequestMapping(value = "/copy/product", method = RequestMethod.POST)
    public CommonResult<Map<String, Object>> copyProduct(@RequestBody @Valid StoreCopyProductRequest request) {
        return CommonResult.success(storeProductService.copyProduct(request.getUrl()));
    }
}



