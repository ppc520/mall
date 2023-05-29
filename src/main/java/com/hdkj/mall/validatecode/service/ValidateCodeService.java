package com.hdkj.mall.validatecode.service;

import com.hdkj.mall.validatecode.model.ValidateCode;

/**
 * ValidateCodeService 接口
 */
public interface ValidateCodeService {

    ValidateCode get();

    boolean check(ValidateCode validateCode);
}