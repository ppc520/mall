package com.hdkj.mall.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.log.model.StoreProductLog;

/**
 * StoreProductLogService 接口
 */
public interface StoreProductLogService extends IService<StoreProductLog> {

    Integer getCountByTimeAndType(String time, String type);

    void addLogTask();
}