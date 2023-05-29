package com.hdkj.mall.front.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.CommonResult;
import com.constants.Constants;
import com.exception.MallException;
import com.hdkj.mall.front.request.OrderPayRequest;
import com.hdkj.mall.payment.wechat.WeChatPayService;
import com.hdkj.mall.finance.model.UserExtract;
import com.hdkj.mall.payment.service.OrderPayService;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.user.model.User;
import com.utils.MallUtil;
import com.utils.DateUtil;
import com.hdkj.mall.finance.service.UserExtractService;
import com.hdkj.mall.front.response.OrderPayResultResponse;
import com.hdkj.mall.store.model.StoreOrder;
import com.hdkj.mall.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信缓存表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/front/pay")
@Api(tags = "支付管理")
public class PayController {

    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private OrderPayService orderPayService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserExtractService userExtractService;
    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 订单支付
     */
    @ApiOperation(value = "订单支付")
    @RequestMapping(value = "/payment", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> payment(@RequestBody @Validated OrderPayRequest orderPayRequest, HttpServletRequest request) {
        String ip = MallUtil.getClientIp(request);
        return CommonResult.success(orderPayService.payment(orderPayRequest, ip));
    }

    /**
     * 订单支付
     */
    @ApiOperation(value = "订单支付")
    @RequestMapping(value = "/payment1", method = RequestMethod.POST)
    public CommonResult<OrderPayResultResponse> payment1(@RequestBody @Validated OrderPayRequest orderPayRequest, HttpServletRequest request) {
        String ip = MallUtil.getClientIp(request);
        return CommonResult.success(orderPayService.payment1(orderPayRequest, ip));
    }


    /**
     * 查询支付结果
     *
     * @param orderNo |订单编号|String|必填
     */
    @ApiOperation(value = "查询支付结果")
    @RequestMapping(value = "/queryPayResult", method = RequestMethod.GET)
    public CommonResult<Boolean> queryPayResult(@RequestParam String orderNo) {
        return CommonResult.success(weChatPayService.queryPayResult(orderNo));
    }

    /**
     * 用户提现
     */
    @ApiOperation(value = "用户提现")
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public CommonResult<Map<String,Object>> transfer(@RequestBody @Validated StoreOrder storeOrder, HttpServletRequest request) {
        Map<String,Object> map = new HashMap<>();
        User user = userService.getById(storeOrder.getUid());
        if(user==null){
            throw new MallException("参数错误，提现失败！");
        }
        if(storeOrder.getPayPrice().doubleValue()<10){
            throw new MallException("提现金额最少10元。");
        }
        if(storeOrder.getPayPrice().doubleValue()>200){
            throw new MallException("提现金额不能超过200元。");
        }

        QueryWrapper<UserExtract> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid",user.getUid());
        queryWrapper.in("status",0,1);
        String date = DateUtil.nowDateTime("yyyy-MM-dd");
        queryWrapper.eq("LEFT(create_time,10)",date);
        List<UserExtract> list =  userExtractService.list(queryWrapper);
        if(list !=null && list.size()>0){
            if(list.size()==10){
                throw new MallException("当日已提现10次。");
            }
            double extractPrice=0.00;
            for (UserExtract userExtract :list){
                extractPrice =extractPrice+ userExtract.getExtractPrice().doubleValue();
            }
            if(extractPrice>200){
                throw new MallException("当日提现金额已超过200元。");
            }else{
                extractPrice =extractPrice+storeOrder.getPayPrice().doubleValue();
                if(extractPrice>200){
                    throw new MallException("当日提现金额不能超过200元。");
                }
            }
        }
        Double transfer_bfb =1-Double.valueOf(systemConfigService.getValueByKey(Constants.CONFIG_TRANSFER_BFB))*0.01;
        Double price =storeOrder.getPayPrice().doubleValue()*100;
        if(transfer_bfb>0){
            price =price*transfer_bfb;
        }

        map.put("uid",user.getUid());
        map.put("name",user.getNickname());
        map.put("nonce_str", DigestUtils.md5Hex(MallUtil.getUuid() + MallUtil.randomCount(111111, 666666)));
        map.put("partner_trade_no",DigestUtils.md5Hex(MallUtil.getUuid() + MallUtil.randomCount(111111, 666666)));
        map.put("price",price.intValue());
        map.put("desc","用户提现");
        map.put("ip", MallUtil.getClientIp(request));
        JSONObject result = weChatPayService.transfer_v3(map);

        UserExtract userExtract = new UserExtract();
        userExtract.setExtractType("weixin");
        userExtract.setExtractPrice(new BigDecimal(String.valueOf(storeOrder.getPayPrice().doubleValue()*transfer_bfb)));
        userExtract.setUid(user.getUid());
        userExtract.setRealName(user.getNickname());
        userExtract.setMark("用户提现");
        BigDecimal money = user.getNowMoney();//可提现总金额
        Double balance = money.doubleValue()-storeOrder.getPayPrice().doubleValue()*transfer_bfb;
        userExtract.setBalance(new BigDecimal(String.valueOf(balance)));
        userExtract.setOutBatchNo(result.get("partner_trade_no").toString());

        if(Integer.valueOf(result.get("code").toString()) ==200){

            userExtract.setStatus(0);
            userExtractService.save(userExtract);

            user.setNowMoney(userExtract.getBalance());
            Double b = user.getBrokeragePrice().doubleValue() + userExtract.getExtractPrice().doubleValue();
            user.setBrokeragePrice(new BigDecimal(String.valueOf(b)));
            userService.updateById(user);
            return CommonResult.success(result);
        }else{
//            userExtract.setStatus(-1);
//            userExtractService.save(userExtract);
            return CommonResult.failed(result.toString());
        }
    }
    /**
     * 查询提现结果
     *
     * @param outBatchNo |订单编号|String|必填
     */
    @ApiOperation(value = "查询提现结果")
    @RequestMapping(value = "/queryTransferResult", method = RequestMethod.GET)
    public CommonResult<Integer> queryTransferResult(@RequestParam String outBatchNo) {
        return CommonResult.success(weChatPayService.queryTransferResult(outBatchNo));
    }
    /**
     * 微信商户证书下载
     */
    @ApiOperation(value = "微信商户证书下载")
    @RequestMapping(value = "/zsxz", method = RequestMethod.GET)
    public CommonResult<Boolean> zsxz() {
        weChatPayService.zsxz();
        return CommonResult.success();
    }
}
