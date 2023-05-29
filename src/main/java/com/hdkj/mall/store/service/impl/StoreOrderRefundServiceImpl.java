package com.hdkj.mall.store.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.constants.PayConstants;
import com.constants.WeChatConstants;
import com.exception.MallException;
import com.hdkj.mall.payment.vo.wechat.WxRefundResponseVo;
import com.hdkj.mall.payment.vo.wechat.WxRefundVo;
import com.hdkj.mall.store.dao.StoreOrderDao;
import com.hdkj.mall.store.model.StoreOrder;
import com.hdkj.mall.store.request.StoreOrderRefundRequest;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.store.service.StoreOrderRefundService;
import com.utils.MallUtil;
import com.utils.RestTemplateUtil;
import com.utils.WxPayUtil;
import com.utils.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * StoreOrderServiceImpl 接口实现
 */
@Service
public class StoreOrderRefundServiceImpl extends ServiceImpl<StoreOrderDao, StoreOrder> implements StoreOrderRefundService {

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    /**
     * 退款 需要优化
     * @author Mr.Zhou
     * @since 2022-06-03
     */
    @Override
    public void refund(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        refundWx(request, storeOrder);
    }

    /**
     * 公共号退款
     * @param request
     * @param storeOrder
     */
    private void refundWx(StoreOrderRefundRequest request, StoreOrder storeOrder) {
        // 获取appid、mch_id
        // 微信签名key
        String appId = "";
        String mchId = "";
        String signKey = "";
        String path = "";
        if (storeOrder.getIsChannel() == 0) {// 公众号
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
//            path = systemConfigService.getValueByKeyException("pay_routine_client_p12");
            path = systemConfigService.getValueByKeyException("pay_weixin_certificate_path");
        }
        if (storeOrder.getIsChannel() == 1) {// 小程序
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
//            path = systemConfigService.getValueByKeyException("pay_mini_client_p12");
            path = systemConfigService.getValueByKeyException("pay_routine_certificate_path");
        }
        if (storeOrder.getIsChannel() == 2) {// H5, 使用公众号的
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
//            path = systemConfigService.getValueByKeyException("pay_mini_client_p12");
            path = systemConfigService.getValueByKeyException("pay_weixin_certificate_path");
        }
        if (storeOrder.getIsChannel() == 4 || storeOrder.getIsChannel() == 5) {// 微信App
            appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_ID);
            mchId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_MCH_ID);
            signKey = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_APP_KEY);
//            path = systemConfigService.getValueByKeyException("pay_mini_app_client_p12");
            path = systemConfigService.getValueByKeyException("pay_weixin_app_certificate_path");
        }

        String apiDomain = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_API_URL);

        //统一下单数据
        WxRefundVo wxRefundVo = new WxRefundVo();
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(WxPayUtil.getNonceStr());
        wxRefundVo.setOut_trade_no(storeOrder.getOrderId());
        wxRefundVo.setOut_refund_no(storeOrder.getOrderId());
        wxRefundVo.setTotal_fee(storeOrder.getPayPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(request.getAmount().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        if(request.getType()==1){
            wxRefundVo.setNotify_url(apiDomain + PayConstants.WX_PAY_FW_REFUND_NOTIFY_API_URI);
        }else{
            wxRefundVo.setNotify_url(apiDomain + PayConstants.WX_PAY_REFUND_NOTIFY_API_URI);
        }
        String sign = WxPayUtil.getSign(wxRefundVo, signKey);
        wxRefundVo.setSign(sign);

        commonRefound(wxRefundVo, path);
    }

    /**
     * 公共退款部分
     * @param wxRefundVo
     * @return
     */
    private WxRefundResponseVo commonRefound(WxRefundVo wxRefundVo, String path) {
        String xmlStr = XmlUtil.objectToXml(wxRefundVo);
        String url = WeChatConstants.PAY_API_URL + WeChatConstants.PAY_REFUND_API_URI_WECHAT;
        HashMap<String, Object> map = CollUtil.newHashMap();
        String xml = "";
        System.out.println("微信申请退款xmlStr = " + xmlStr);
        try {
            xml = restTemplateUtil.postWXRefundXml(url, xmlStr, wxRefundVo.getMch_id(), path);
            map = XmlUtil.xmlToMap(xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MallException("xmlToMap错误，xml = " + xml);
        }
        if(null == map){
            throw new MallException("微信申请退款失败！");
        }

        WxRefundResponseVo responseVo = MallUtil.mapToObj(map, WxRefundResponseVo.class);
        if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
            throw new MallException("微信申请退款失败1！" +  responseVo.getReturnMsg());
        }

        if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
            throw new MallException("微信申请退款失败2！" + responseVo.getErrCodeDes());
        }
        System.out.println("================微信申请退款结束=========================");
        System.out.println("xml = " + xml);
        return responseVo;
    }
}

