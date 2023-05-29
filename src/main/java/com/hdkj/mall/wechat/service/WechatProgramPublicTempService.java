package com.hdkj.mall.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.wechat.request.WechatProgramPublicTempSearchRequest;
import com.hdkj.mall.wechat.model.WechatProgramPublicTemp;

import java.util.List;

/**
 *  WechatProgramPublicTempService 接口
 */
public interface WechatProgramPublicTempService extends IService<WechatProgramPublicTemp> {

    List<WechatProgramPublicTemp> getList(WechatProgramPublicTempSearchRequest request, PageParamRequest pageParamRequest);

    void async();
}