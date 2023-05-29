package com.hdkj.mall.wechat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.hdkj.mall.wechat.dao.WechatQrcodeDao;
import com.hdkj.mall.wechat.model.WechatQrcode;
import com.hdkj.mall.wechat.request.WechatQrcodeSearchRequest;
import com.hdkj.mall.wechat.service.WechatQrcodeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * WechatQrcodeServiceImpl 接口实现
 */
@Service
public class WechatQrcodeServiceImpl extends ServiceImpl<WechatQrcodeDao, WechatQrcode> implements WechatQrcodeService {

    @Resource
    private WechatQrcodeDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhou
    * @since 2022-04-18
    * @return List<WechatQrcode>
    */
    @Override
    public List<WechatQrcode> getList(WechatQrcodeSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        return dao.selectList(null);
    }

}

