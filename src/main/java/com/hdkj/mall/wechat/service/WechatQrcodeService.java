package com.hdkj.mall.wechat.service;

import com.common.PageParamRequest;
import com.hdkj.mall.wechat.request.WechatQrcodeSearchRequest;
import com.hdkj.mall.wechat.model.WechatQrcode;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *  WechatQrcodeService 接口
 */
public interface WechatQrcodeService extends IService<WechatQrcode> {

    List<WechatQrcode> getList(WechatQrcodeSearchRequest request, PageParamRequest pageParamRequest);
}