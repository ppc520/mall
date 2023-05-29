package com.hdkj.mall.payment.wechat;

import com.alibaba.fastjson.JSONObject;
import com.hdkj.mall.finance.model.UserRecharge;
import com.hdkj.mall.payment.vo.wechat.CreateOrderResponseVo;
import com.hdkj.mall.payment.vo.wechat.PayParamsVo;
import com.hdkj.mall.store.model.StoreOrder;
import com.hdkj.mall.user.model.UserLjhsOrder;

import java.util.Map;

/**
 * 微信支付
 */
public interface WeChatPayService {
    CreateOrderResponseVo create(PayParamsVo payParamsVo);

    /**
     * 微信预下单接口
     * @param storeOrder 订单
     * @param ip      ip
     * @return 获取wechat.requestPayment()参数
     */
    Map<String, String> unifiedorder(StoreOrder storeOrder, String ip);

    /**
     * 微信预下单接口
     * @param userLjhsOrder 订单
     * @param ip      ip
     * @return 获取wechat.requestPayment()参数
     */
    Map<String, String> unifiedorder1(UserLjhsOrder userLjhsOrder, String ip);

    /**
     * 查询支付结果
     * @param orderNo 订单编号
     * @return
     */
    Boolean queryPayResult(String orderNo);

    /**
     * 微信充值预下单接口
     * @param userRecharge 充值订单
     * @param clientIp      ip
     * @return 获取wechat.requestPayment()参数
     */
    Map<String, String> unifiedRecharge(UserRecharge userRecharge, String clientIp);


    /**
     * 用户提现
     * @param map
     */
    JSONObject transfer_v3(Map<String,Object> map);


    /**
     * 查询提现结果
     * @param outBatchNo 订单编号
     * @return
     */
    Integer queryTransferResult(String outBatchNo);

    /**
     * 同步提现状态
     */
    void sysTransferStatus();

    void zsxz();

}
