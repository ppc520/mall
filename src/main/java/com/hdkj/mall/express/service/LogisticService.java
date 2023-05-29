package com.hdkj.mall.express.service;

import com.hdkj.mall.express.vo.LogisticsResultVo;

/**
* ExpressService 接口
*/
public interface LogisticService {
    LogisticsResultVo info(String expressNo, String type, String com, String phone);
}
