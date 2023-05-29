package com.hdkj.mall.store.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.github.pagehelper.PageHelper;
import com.hdkj.mall.store.dao.StoreProductAttrResultDao;
import com.hdkj.mall.store.model.StoreProductAttrResult;
import com.hdkj.mall.store.request.StoreProductAttrResultSearchRequest;
import com.hdkj.mall.store.service.StoreProductAttrResultService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * StoreProductAttrResultService实现类
 */
@Service
public class StoreProductAttrResultServiceImpl extends ServiceImpl<StoreProductAttrResultDao, StoreProductAttrResult>
        implements StoreProductAttrResultService {

    @Resource
    private StoreProductAttrResultDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhou
    * @since 2022-05-27
    * @return List<StoreProductAttrResult>
    */
    @Override
    public List<StoreProductAttrResult> getList(StoreProductAttrResultSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 StoreProductAttrResult 类的多条件查询
        LambdaQueryWrapper<StoreProductAttrResult> lambdaQueryWrapper = Wrappers.lambdaQuery();
        StoreProductAttrResult model = new StoreProductAttrResult();
        BeanUtils.copyProperties(request, model);
        lambdaQueryWrapper.setEntity(model);
        return dao.selectList(lambdaQueryWrapper);
    }

    @Override
    public StoreProductAttrResult getByProductId(int productId) {
        LambdaQueryWrapper<StoreProductAttrResult> lw = Wrappers.lambdaQuery();
        lw.eq(StoreProductAttrResult::getProductId, productId);
        List<StoreProductAttrResult> results = dao.selectList(lw);
        if(results.size() > 1){
            return results.get(results.size()-1);
        }else if(results.size() == 1){
            return results.get(0);
        }else{
            return null;
        }
    }

    @Override
    public void deleteByProductId(int productId, int type) {
        LambdaQueryWrapper<StoreProductAttrResult> lmdQ = Wrappers.lambdaQuery();
        lmdQ.eq(StoreProductAttrResult::getProductId, productId).eq(StoreProductAttrResult::getType,type);
        dao.delete(lmdQ);
    }

    /**
     * 根据商品属性值集合查询
     *
     * @param storeProductAttrResult 查询参数
     * @return 查询结果
     */
    @Override
    public List<StoreProductAttrResult> getByEntity(StoreProductAttrResult storeProductAttrResult) {
        LambdaQueryWrapper<StoreProductAttrResult> lqw = Wrappers.lambdaQuery();
        lqw.setEntity(storeProductAttrResult);
        return dao.selectList(lqw);
    }
}

