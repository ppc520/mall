package com.hdkj.mall.wechat.service;

import com.common.PageParamRequest;
import com.hdkj.mall.wechat.model.WechatReply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.wechat.request.WechatReplySearchRequest;

import java.util.List;

/**
 *  WechatReplyService 接口
 */
public interface WechatReplyService extends IService<WechatReply> {

    List<WechatReply> getList(WechatReplySearchRequest request, PageParamRequest pageParamRequest);

    Boolean create(WechatReply wechatReply);

    Boolean updateVo(WechatReply wechatReply);

    WechatReply getVoByKeywords(String keywords);

    WechatReply getInfoException(Integer id, boolean isTrue);

    WechatReply getInfo(Integer id);
}