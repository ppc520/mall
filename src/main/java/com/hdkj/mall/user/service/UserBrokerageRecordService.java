package com.hdkj.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.front.response.SpreadCommissionDetailResponse;
import com.hdkj.mall.user.model.UserBrokerageRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户佣金记录服务接口
 */
public interface UserBrokerageRecordService extends IService<UserBrokerageRecord> {

    /**
     * 获取记录列表
     * @param linkId 关联id
     * @param linkType 关联类型
     * @return 记录列表
     */
    List<UserBrokerageRecord> findListByLinkIdAndLinkType(String linkId, String linkType);

    /**
     * 获取记录(订单不可用此方法)
     * @param linkId 关联id
     * @param linkType 关联类型
     * @return 记录列表
     */
    UserBrokerageRecord getByLinkIdAndLinkType(String linkId, String linkType);

    /**
     * 佣金解冻
     */
    void brokerageThaw();

    /**
     * 昨天得佣金
     * @param uid 用户uid
     */
    BigDecimal getYesterdayIncomes(Integer uid);

    /**
     * 获取佣金明细列表根据uid
     * @param uid uid
     * @param pageParamRequest 分页参数
     */
    PageInfo<SpreadCommissionDetailResponse> findDetailListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取累计推广条数
     * @param uid 用户uid
     * @return Integer
     */
    Integer getSpreadCountByUid(Integer uid);

    /**
     * 获取推广记录列表
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserBrokerageRecord> findSpreadListByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 获取月份对应的推广订单数
     * @param uid 用户uid
     * @param monthList 月份列表
     * @return Map
     */
    Map<String, Integer> getSpreadCountByUidAndMonth(Integer uid, List<String> monthList);

    /**
     * 获取佣金排行榜（周、月）
     * @param type week、month
     * @param pageParamRequest 分页参数
     * @return List
     */
    List<UserBrokerageRecord> getBrokerageTopByDate(String type, PageParamRequest pageParamRequest);

    /**
     * 根据Uid和时间参数获取分佣记录列表
     * @param uid 用户uid
     * @return List
     */
    List<UserBrokerageRecord> getSpreadListByUid(Integer uid);

    /**
     * 佣金总金额（单位时间）
     * @param dateLimit 时间参数
     * @return BigDecimal
     */
    BigDecimal getTotalSpreadPriceBydateLimit(String dateLimit);

    /**
     * 单位时间消耗的佣金
     * @param dateLimit 时间参数
     * @return BigDecimal
     */
    BigDecimal getSubSpreadPriceByDateLimit(String dateLimit);

    /**
     * 佣金详情列表
     * @param uid uid
     * @param dateLimit 时间参数
     * @param pageParamRequest 分页参数
     * @return PageInfo
     */
    PageInfo<UserBrokerageRecord> getFundsMonitorDetail(Integer uid, String dateLimit, PageParamRequest pageParamRequest);

    /**
     * 获取冻结期佣金
     * @param uid uid
     * @return BigDecimal
     */
    BigDecimal getFreezePrice(Integer uid);

    /**
     * 获取记录列表
     * @param linkIds 关联id集合
     * @param linkType 关联类型
     * @param uid 用户uid
     * @param pageParamRequest 分页参数
     * @return 记录列表
     */
    PageInfo<UserBrokerageRecord> findListByLinkIdsAndLinkTypeAndUid(List<String> linkIds, String linkType, Integer uid, PageParamRequest pageParamRequest);
}