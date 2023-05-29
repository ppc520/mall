package com.hdkj.mall.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.constants.Constants;
import com.hdkj.mall.system.dao.SystemCityDao;
import com.hdkj.mall.system.model.SystemCity;
import com.hdkj.mall.system.request.SystemCityRequest;
import com.hdkj.mall.system.request.SystemCitySearchRequest;
import com.hdkj.mall.system.service.SystemCityService;
import com.utils.RedisUtil;
import com.hdkj.mall.system.service.SystemCityAsyncService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * SystemCityServiceImpl 接口实现
 */
@Service
public class SystemCityServiceImpl extends ServiceImpl<SystemCityDao, SystemCity> implements SystemCityService {

    @Resource
    private SystemCityDao dao;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SystemCityAsyncService systemCityAsyncService;

    /**
     * 列表
     * @param request 请求参数
     * @author Mr.Zhou
     * @since 2022-04-17
     * @return List<SystemCity>
     */
    @Override
    public Object getList(SystemCitySearchRequest request) {
        if(redisUtil.hmGet(Constants.CITY_LIST, request.getParentId().toString()) == null){
            asyncRedis(request.getParentId());
        }
        Object list = redisUtil.hmGet(Constants.CITY_LIST, request.getParentId().toString());
        if(null == list){
            //城市数据，异步同步到redis，第一次拿不到数据，去数据库读取
            list = getList(request.getParentId());
        }

        return list;
    }

    /**
     * 根据父级id获取数据
     * @param parentId integer parentId
     * @author Mr.Zhou
     * @since 2022-04-17
     * @return Object
     */
    private Object getList(Integer parentId) {
        LambdaQueryWrapper<SystemCity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemCity::getParentId, parentId);
        lambdaQueryWrapper.in(SystemCity::getIsShow, true);
        return dao.selectList(lambdaQueryWrapper);
    }
    /**
     * 修改状态
     * @param id integer id
     * @param status boolean 状态
     * @author Mr.Zhou
     * @since 2022-04-17
     * @return bool
     */
    @Override
    public boolean updateStatus(Integer id, Boolean status){
        SystemCity systemCity = getById(id);
        systemCity.setId(id);
        systemCity.setIsShow(status);
        boolean result = updateById(systemCity);
        asyncRedis(systemCity.getParentId());
        return result;
    }

    /**
     * 修改城市
     * @param id integer id
     * @param request 修改参数
     * @author Mr.Zhou
     * @since 2022-04-17
     * @return bool
     */
    @Override
    public boolean update(Integer id, SystemCityRequest request) {
        SystemCity systemCity = new SystemCity();
        BeanUtils.copyProperties(request, systemCity);
        systemCity.setId(id);
        boolean result = updateById(systemCity);
        asyncRedis(request.getParentId());
        return result;
    }
    /**
     * 获取tree结构的列表
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return Object
     */
    @Override
    public Object getListTree() {
        Object cityList = redisUtil.get(Constants.CITY_LIST_TREE);
        if(null == cityList){
            systemCityAsyncService.setListTree();
        }
        return redisUtil.get(Constants.CITY_LIST_TREE);
    }

    /**
     * 获取所有城市cityId
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return List<Integer>
     */
    @Override
    public List<Integer> getCityIdList() {
        Object data = redisUtil.get(Constants.CITY_LIST_LEVEL_1);
        List<Integer> collect;
        if(data == null || data.equals("")){
            LambdaQueryWrapper<SystemCity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.select(SystemCity::getCityId);
            lambdaQueryWrapper.eq(SystemCity::getLevel, 1);
            lambdaQueryWrapper.eq(SystemCity::getIsShow, true);
            List<SystemCity> systemCityList = dao.selectList(lambdaQueryWrapper);
            collect = systemCityList.stream().map(SystemCity::getCityId).distinct().collect(Collectors.toList());
            redisUtil.set(Constants.CITY_LIST_LEVEL_1, collect, 10L, TimeUnit.MINUTES);
        }else{
            collect = (List<Integer>) data;
        }

        return collect;
    }

    /**
     * 根据city_id获取城市信息
     * @author Mr.Zhou
     * @since 2022-05-18
     */
    @Override
    public SystemCity getCityByCityId(Integer cityId) {
        LambdaQueryWrapper<SystemCity> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemCity::getCityId, cityId).eq(SystemCity::getIsShow, 1);
        return getOne(lambdaQueryWrapper);
    }


    /**
     * 数据整体刷入redis
     * @author Mr.Zhou
     * @since 2022-05-18
     */
    public void asyncRedis(Integer pid){
        systemCityAsyncService.async(pid);
    }

    /**
     * 根据城市名称获取城市详细数据
     * @author 大粽子
     * @param cityName 城市名称
     * @return 城市数据微信下单失败
     */
    @Override
    public SystemCity getCityByCityName(String cityName) {
        LambdaQueryWrapper<SystemCity> systemCityLambdaQueryWrapper = Wrappers.lambdaQuery();
        systemCityLambdaQueryWrapper
                .eq(SystemCity::getName,cityName)
                .eq(SystemCity::getIsShow,1)
                .gt(SystemCity::getLevel,0);

        return getOne(systemCityLambdaQueryWrapper);
    }
}

