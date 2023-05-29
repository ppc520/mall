package com.hdkj.mall.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.hdkj.mall.wechat.response.WeChatAuthorizeLoginGetOpenIdResponse;
import com.hdkj.mall.wechat.response.WeChatProgramAuthorizeLoginGetOpenIdResponse;
import com.hdkj.mall.front.response.UserRechargePaymentResponse;
import com.hdkj.mall.payment.vo.wechat.CreateOrderResponseVo;
import com.hdkj.mall.wechat.response.WeChatAuthorizeLoginUserInfoResponse;
import com.hdkj.mall.wechat.vo.*;

import java.util.HashMap;
import java.util.List;

/**
 * WeChatPublicService 接口
 */
public interface WeChatService {

    JSONObject get();

    JSONObject create(String data);

    JSONObject delete();

    JSONObject getSelf();

    JSONObject createSelf(String data);

    JSONObject deleteSelf(String menuId);

    void pushKfMessage(HashMap<String, Object> map);

    JSONObject createTags(String name);

    JSONObject getTagsList();

    JSONObject updateTags(String id, String name);

    JSONObject deleteTags(String id);

    JSONObject getUserListByTagsId(String id, String nextOpenId);

    JSONObject memberBatchTags(String id, String data);

    JSONObject memberBatchUnTags(String id, String data);

    JSONObject getTagsListByUserId(String openId);

    WeChatAuthorizeLoginGetOpenIdResponse authorizeLogin(String code);

    WeChatAuthorizeLoginUserInfoResponse getUserInfo(String openId, String token);

    Object getJsSdkConfig(String url);

    boolean sendPublicTempMessage(TemplateMessageVo templateMessage);

    boolean sendProgramTempMessage(TemplateMessageVo templateMessage);

    JSONObject getIndustry();

    String getUploadMedia();

    WeChatProgramAuthorizeLoginGetOpenIdResponse programAuthorizeLogin(String code);

    String qrCode(String page, String uri);

    UserRechargePaymentResponse response(CreateOrderResponseVo responseVo);

    List<ProgramCategoryVo> getProgramCategory();

    List<ProgramTempVo> getProgramPublicTempList(int page);

    List<ProgramTempKeywordsVo> getWeChatKeywordsByTid(Integer tid);

    String programAddMyTemp(ProgramAddMyTempVo programAddMyTempVo);

    void programDeleteMyTemp(String myTempId);

    String getRoutineAccessToken();
}