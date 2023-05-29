package com.hdkj.mall.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.hdkj.mall.store.dao.StoreProductCateDao;
import com.hdkj.mall.store.model.StoreProductCate;
import com.hdkj.mall.store.request.StoreProductCateSearchRequest;
import com.hdkj.mall.store.service.StoreProductCateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductCateService 实现类
 */
@Service
public class StoreProductCateServiceImpl extends ServiceImpl<StoreProductCateDao, StoreProductCate>
        implements StoreProductCateService {

    @Resource
    private StoreProductCateDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhou
    * @since 2022-05-27
    * @return List<StoreProductCate>
    */
    @Override
    public List<StoreProductCate> getList(StoreProductCateSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductCate 类的多条件查询
        LambdaQueryWrapper<StoreProductCate> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        StoreProductCate model = new StoreProductCate();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public List<StoreProductCate> getByProductId(Integer productId) {
        LambdaQueryWrapper<StoreProductCate> lqw = new LambdaQueryWrapper<>();
        lqw.eq(StoreProductCate::getProductId, productId);
        return dao.selectList(lqw);
    }

}

