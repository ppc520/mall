package com.hdkj.mall.finance.service;

import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.finance.request.UserExtractSearchRequest;
import com.hdkj.mall.finance.response.BalanceResponse;
import com.hdkj.mall.finance.response.UserExtractResponse;
import com.hdkj.mall.finance.model.UserExtract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hdkj.mall.finance.request.UserExtractRequest;
import com.hdkj.mall.front.response.UserExtractRecordResponse;

import java.math.BigDecimal;
import java.util.List;

/**
* UserExtractService 接口
*/
public interface UserExtractService extends IService<UserExtract> {

    List<UserExtract> getList(UserExtractSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 提现总金额
     */
    BalanceResponse getBalance(String dateLimit);

    /**
     * 提现总金额
     * @author Mr.Zhou
     * @since 2022-05-11
     * @return BalanceResponse
     */
    BigDecimal getWithdrawn(String startTime,String endTime);

    /**
     * 审核中总金额
     * @author Mr.Zhou
     * @since 2022-05-11
     * @return BalanceResponse
     */
    BigDecimal getWithdrawning(String startTime, String endTime);

    Boolean create(UserExtractRequest request, Integer userId);

    BigDecimal getFreeze(Integer userId);

    UserExtractResponse getUserExtractByUserId(Integer userId);

    List<UserExtract> getListByUserIds(List<Integer> userIds);

    /**
     * 提现审核
     * @param id    提现申请id
     * @param status 审核状态 -1 未通过 0 审核中 1 已提现
     * @param backMessage   驳回原因
     * @return  审核结果
     */
    Boolean updateStatus(Integer id,Integer status,String backMessage);

    PageInfo<UserExtractRecordResponse> getExtractRecord(Integer userId, PageParamRequest pageParamRequest);

    BigDecimal getExtractTotalMoney(Integer userId);

    /**
     * 提现申请
     * @return
     */
    Boolean extractApply(UserExtractRequest request);
}
