package com.hdkj.mall.payment.controller;

import com.hdkj.mall.payment.service.CallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * 支付回调
 */
@Slf4j
@RestController
@RequestMapping("api/admin/payment/callback")
@Api(tags = "支付回调")
public class CallbackController {

    @Autowired
    private CallbackService callbackService;

    /**
     * 微信支付回调
     * @author Mr.Zhou
     * @since 2022-05-06
     * @return
     */
    @ApiOperation(value = "微信支付回调")
    @RequestMapping(value = "/wechat", method = RequestMethod.POST)
    public String weChat(@RequestBody String  request) {
        System.out.println("微信支付回调 request ===> " + request);
        String response = callbackService.weChat(request);
        System.out.println("微信支付回调 response ===> " + response);
        return response;
    }

    /**
     * 支付宝支付回调
     * @author Mr.Zhou
     * @since 2022-05-06
     */
    @ApiOperation(value = "支付宝支付回调 ")
    @RequestMapping(value = "/alipay", method = RequestMethod.POST)
    public void aliPay(@RequestBody String request){
        //支付宝支付回调
        callbackService.aliPay(request);
    }

    /**
     * 微信退款回调
     */
    @ApiOperation(value = "微信退款回调-商品订单")
    @RequestMapping(value = "/wechat/refund", method = RequestMethod.POST)
    public String weChatRefund(@RequestBody String request) {
        System.out.println("微信退款回调 request ===> " + request);
        String response = callbackService.weChatRefund(request);
        System.out.println("微信退款回调 response ===> " + response);
        return response;
    }
    /**
     * 微信退款回调
     */
    @ApiOperation(value = "微信退款回调-服务订单")
    @RequestMapping(value = "/wechat/refund1", method = RequestMethod.POST)
    public String weChatRefund1(@RequestBody String request) {
        System.out.println("微信退款回调 request ===> " + request);
        String response = callbackService.weChatRefund1(request);
        System.out.println("微信退款回调 response ===> " + response);
        return response;
    }
}



