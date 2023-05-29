package com.hdkj.mall.store.controller;

import com.common.CheckAdminToken;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.hdkj.mall.express.vo.ExpressSheetVo;
import com.hdkj.mall.express.vo.LogisticsResultVo;
import com.hdkj.mall.store.request.*;
import com.hdkj.mall.store.response.*;
import com.hdkj.mall.store.service.StoreOrderService;
import com.hdkj.mall.store.service.StoreOrderVerification;
import com.hdkj.mall.system.response.SystemAdminResponse;
import com.hdkj.mall.system.service.SystemAdminService;
import com.hdkj.mall.user.request.UserLjhsOrdeRequest;
import com.hdkj.mall.user.request.UserOthersOrdeRequest;
import com.hdkj.mall.user.response.UserLjhsOrderNum;
import com.hdkj.mall.user.response.UserLjhsOrderResponse;
import com.hdkj.mall.user.response.UserOthersOrderResponse;
import com.hdkj.mall.user.service.UserLjhsOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/store/order")
@Api(tags = "订单") //配合swagger使用
public class StoreOrderController {

    @Autowired
    private StoreOrderService storeOrderService;

    @Autowired
    private StoreOrderVerification storeOrderVerification;
    @Autowired
    private UserLjhsOrderService userLjhsOrderService;

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private CheckAdminToken checkAdminToken;

    /**
     * 分页显示订单表
     *  @param storeOrderSearchRequest          搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表") //配合swagger使用
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<StoreOrderDetailResponse>> getList(@Validated StoreOrderSearchRequest storeOrderSearchRequest, @Validated PageParamRequest pageParamRequest, HttpServletRequest request) {
        try {
            String token = checkAdminToken.getTokenFormRequest(request);
            SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);
            if(systemAdminResponse!=null){
                if(systemAdminResponse.getRoles().equals("7")){
                    storeOrderSearchRequest.setMerId(systemAdminResponse.getId());
                }
            }
        }catch (Exception e){}

        return CommonResult.success(storeOrderService.getAdminList(storeOrderSearchRequest, pageParamRequest));
    }

    /**
     * 获取订单各状态数量
     */
    @ApiOperation(value = "获取订单各状态数量")
    @RequestMapping(value = "/status/num", method = RequestMethod.GET)
    public CommonResult<StoreOrderCountItemResponse> getOrderStatusNum(@RequestParam(value = "dateLimit", defaultValue = "") String dateLimit, HttpServletRequest request) {
        Integer merId =0;
        try {
            String token = checkAdminToken.getTokenFormRequest(request);
            SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);
            if(systemAdminResponse!=null){
                if(systemAdminResponse.getRoles().equals("7")){
                    merId = systemAdminResponse.getId();
                }
            }
        }catch (Exception e){}
        return CommonResult.success(storeOrderService.getOrderStatusNum(merId,dateLimit));
    }

    /**
     * 获取订单统计数据
     */
    @ApiOperation(value = "获取订单统计数据")
    @RequestMapping(value = "/list/data", method = RequestMethod.GET)
    public CommonResult<StoreOrderTopItemResponse> getOrderData(@RequestParam(value = "dateLimit", defaultValue = "")String dateLimit, HttpServletRequest request) {
        Integer merId =0;
        try {
            String token = checkAdminToken.getTokenFormRequest(request);
            SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);
            if(systemAdminResponse!=null){
                if(systemAdminResponse.getRoles().equals("7")){
                    merId = systemAdminResponse.getId();
                }
            }
        }catch (Exception e){}
        return CommonResult.success(storeOrderService.getOrderData(merId,dateLimit));
    }


    /**
     * 订单删除
     */
    @ApiOperation(value = "订单删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public CommonResult<String> delete(@RequestParam(value = "orderNo") String orderNo) {
        if (storeOrderService.delete(orderNo)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 备注订单
     */
    @ApiOperation(value = "备注")
    @RequestMapping(value = "/mark", method = RequestMethod.POST)
    public CommonResult<String> mark(@RequestParam String orderNo, @RequestParam String mark) {
        if (storeOrderService.mark(orderNo, mark)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 修改订单(改价)
     */
    @ApiOperation(value = "修改订单(改价)")
    @RequestMapping(value = "/update/price", method = RequestMethod.POST)
    public CommonResult<String> update(@RequestBody @Validated StoreOrderUpdatePriceRequest request) {
        if (storeOrderService.updatePrice(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 订单详情
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    public CommonResult<StoreOrderInfoResponse> info(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(storeOrderService.info(orderNo));
    }

    /**
     * 发送货
     */
    @ApiOperation(value = "发送货")
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public CommonResult<Boolean> send(@RequestBody @Validated StoreOrderSendRequest request) {
        if (storeOrderService.send(request)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 退款
     */
    @ApiOperation(value = "退款")
    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public CommonResult<Boolean> send(@Validated StoreOrderRefundRequest request) {
        return CommonResult.success(storeOrderService.refund(request));
    }

    /**
     * 拒绝退款
     */
    @ApiOperation(value = "拒绝退款")
    @RequestMapping(value = "/refund/refuse", method = RequestMethod.GET)
    public CommonResult<Object> refundRefuse(@RequestParam String orderNo, @RequestParam String reason) {
        if (storeOrderService.refundRefuse(orderNo, reason)) {
            return CommonResult.success();
        }
        return CommonResult.failed();
    }

    /**
     * 快递查询
     */
    @ApiOperation(value = "快递查询")
    @RequestMapping(value = "/getLogisticsInfo", method = RequestMethod.GET)
    public CommonResult<LogisticsResultVo> getLogisticsInfo(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(storeOrderService.getLogisticsInfo(orderNo));
    }

    /**
     * 核销订单头部数据
     *
     * @author stivepeim
     * @since 2022-08-29
     */
    @ApiOperation(value = "核销订单头部数据")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<StoreStaffTopDetail> getStatistics() {
        return CommonResult.success(storeOrderVerification.getOrderVerificationData());
    }

    /**
     * 核销订单 月列表数据
     *
     * @author stivepeim
     * @since 2022-08-29
     */
    @ApiOperation(value = "核销订单 月列表数据")
    @RequestMapping(value = "/statisticsData", method = RequestMethod.GET)
    public CommonResult<List<StoreStaffDetail>> getStaffDetail(StoreOrderStaticsticsRequest request) {
        return CommonResult.success(storeOrderVerification.getOrderVerificationDetail(request));
    }


    /**
     * 核销码核销订单
     *
     * @author stivepeim
     * @since 2022-09-01
     */
    @ApiOperation(value = "核销码核销订单")
    @RequestMapping(value = "/writeUpdate/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationOrder(@PathVariable String vCode) {
        return CommonResult.success(storeOrderVerification.verificationOrderByCode(vCode));
    }

    /**
     * 核销码查询待核销订单
     *
     * @author stivepeim
     * @since 2022-09-01
     */
    @ApiOperation(value = "核销码查询待核销订单")
    @RequestMapping(value = "/writeConfirm/{vCode}", method = RequestMethod.GET)
    public CommonResult<Object> verificationConfirmOrder(
            @PathVariable String vCode) {
        return CommonResult.success(storeOrderVerification.getVerificationOrderByCode(vCode));
    }

    /**
     * 一键改价
     *
     * @author stivepeim
     * @since 2022-09-01
     */
    @ApiOperation(value = "一键改价")
    @RequestMapping(value = "/editPrice", method = RequestMethod.POST)
    public CommonResult<Object> editOrderPrice(@RequestBody @Validated StoreOrderEditPriceRequest request) {
        return CommonResult.success(storeOrderService.editPrice(request));
    }

    /**
     * 订单统计详情
     *
     * @author stivepeim
     * @since 2022-09-01
     */
    @ApiOperation(value = "订单统计详情")
    @RequestMapping(value = "/time", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dateLimit", value = "today,yesterday,lately7,lately30,month,year,/yyyy-MM-dd hh:mm:ss,yyyy-MM-dd hh:mm:ss/",
                    dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "1=price 2=order", required = true)
    })
    public CommonResult<Object> statisticsOrderTime(@RequestParam String dateLimit,
                                                    @RequestParam Integer type) {
        return CommonResult.success(storeOrderService.orderStatisticsByTime(dateLimit, type));
    }

    /**
     * 获取面单默认配置信息
     */
    @ApiOperation(value = "获取面单默认配置信息")
    @RequestMapping(value = "/sheet/info", method = RequestMethod.GET)
    public CommonResult<ExpressSheetVo> getDeliveryInfo() {
        return CommonResult.success(storeOrderService.getDeliveryInfo());
    }

    /**
     * 服务订单详情
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/fwInfo", method = RequestMethod.GET)
    public CommonResult<UserLjhsOrderResponse> fwInfo(@RequestParam(value = "orderNo") String orderNo) {
        return CommonResult.success(userLjhsOrderService.ljdrOrderDetailsByOrderId(orderNo));
    }

    /**
     * 分页显示垃圾订单表
     *  @param request          搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页显示垃圾订单表") //配合swagger使用
    @RequestMapping(value = "/listLj", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserLjhsOrderResponse>> getListLj(
            HttpServletRequest httpServletRequest,
            @Validated UserLjhsOrdeRequest request,
            @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(userLjhsOrderService.getAdminList(request, pageParamRequest));
    }

    /**
     * 获取垃圾订单各状态数量
     */
    @ApiOperation(value = "获取垃圾订单各状态数量")
    @RequestMapping(value = "/status/numLj", method = RequestMethod.GET)
    public CommonResult<UserLjhsOrderNum> getOrderStatusNumLj(HttpServletRequest httpServletRequest,
                                                              @Validated UserLjhsOrdeRequest request) {
        return CommonResult.success(userLjhsOrderService.getOrderStatusNum(request));
    }

    /**
     * 垃圾订单 编辑-分配接单人
     */
    @ApiOperation(value = "垃圾订单 编辑")
    @RequestMapping(value = "/updateLjOrderJDR", method = RequestMethod.POST)
    public CommonResult<String> updateLjOrderJDR(@RequestBody @Validated UserLjhsOrdeRequest request) {
        if (userLjhsOrderService.updateLjOrder(request)) {
            return CommonResult.success();
        } else {
            return CommonResult.failed();
        }
    }

    /**
     * 分页显示预约订单表
     *  @param request          搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页显示预约订单表") //配合swagger使用
    @RequestMapping(value = "/listYY", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserOthersOrderResponse>> getListYy(
            @Validated UserOthersOrdeRequest request,
            @Validated PageParamRequest pageParamRequest) {
        return CommonResult.success(userLjhsOrderService.listAdminOthersOrder(request, pageParamRequest));
    }


}



