package com.hdkj.mall.store.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.hdkj.mall.store.request.RetailShopRequest;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.response.SpreadUserResponse;
import com.hdkj.mall.store.response.RetailShopStatisticsResponse;

/**
 * 分销业务
 */
public interface RetailShopService extends IService<User> {

    /**
     * 分销员列表
     * @param keywords 搜索参数
     * @param dateLimit 时间参数
     * @param pageRequest 分页参数
     * @return
     */
    CommonPage<SpreadUserResponse> getSpreadPeopleList(String keywords, String dateLimit, PageParamRequest pageRequest);

    /**
     * 获取分销配置
     * @return 分销配置信息
     */
    RetailShopRequest getManageInfo();

    /**
     * 保存或者更新分销配置信息
     * @param retailShopRequest 待保存数据
     * @return 保存结果
     */
    boolean setManageInfo(RetailShopRequest retailShopRequest);

    /**
     * 获取分销统计数据
     * @param keywords  模糊搜索参数
     * @param dateLimit 时间参数
     * @return
     */
    RetailShopStatisticsResponse getAdminStatistics(String keywords, String dateLimit);
}
