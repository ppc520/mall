package com.hdkj.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.system.model.SystemFormTemp;
import com.hdkj.mall.system.request.SystemFormCheckRequest;
import com.hdkj.mall.system.request.SystemFormTempSearchRequest;

import java.util.List;

/**
 * SystemFormTempService 接口
 */
public interface SystemFormTempService extends IService<SystemFormTemp> {

    List<SystemFormTemp> getList(SystemFormTempSearchRequest request, PageParamRequest pageParamRequest);

    void checkForm(SystemFormCheckRequest systemFormCheckRequest);
}