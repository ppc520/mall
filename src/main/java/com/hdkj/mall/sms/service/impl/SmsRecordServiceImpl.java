package com.hdkj.mall.sms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.hdkj.mall.sms.dao.SmsRecordDao;
import com.hdkj.mall.sms.request.SmsRecordRequest;
import com.hdkj.mall.sms.model.SmsRecord;
import com.hdkj.mall.sms.service.SmsRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SmsRecordServiceImpl 接口实现
 */
@Service
public class SmsRecordServiceImpl extends ServiceImpl<SmsRecordDao, SmsRecord> implements SmsRecordService {

    @Resource
    private SmsRecordDao dao;

    /**
     * 短信发送记录
     *
     * @param request          请求参数
     * @param pageParamRequest 分页类参数
     */
    @Override
    public List<SmsRecord> getList(SmsRecordRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SmsRecord> queryWrapper = new LambdaQueryWrapper<>();
        if (null != request.getResultCode()) {
            queryWrapper.eq(SmsRecord::getResultcode, request.getResultCode());
        }
        queryWrapper.orderByDesc(SmsRecord::getId);
        return dao.selectList(queryWrapper);
    }

    /**
     * 短信发送记录保存
     *
     * @param smsRecord 待保存短信记录
     * @return 保存结果
     */
    @Override
    public boolean save(SmsRecord smsRecord) {
        return dao.insert(smsRecord) > 0;
    }
}

