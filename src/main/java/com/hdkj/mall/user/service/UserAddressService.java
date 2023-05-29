package com.hdkj.mall.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.front.request.UserAddressRequest;
import com.hdkj.mall.user.model.UserAddress;

import java.util.List;

/**
 * UserAddressService 接口实现
 */
public interface UserAddressService extends IService<UserAddress> {

    /**
     * 用户地址列表
     * @param pageParamRequest 分页参数
     * @return List<UserAddress>
     */
    List<UserAddress> getList(PageParamRequest pageParamRequest);

    /**
     * 根据基本条件查询
     * @param address 查询条件
     * @return 查询到的地址
     */
    UserAddress getUserAddress(UserAddress address);

    UserAddress create(UserAddressRequest request);

    boolean def(Integer id);

    boolean delete(Integer id);

    UserAddress getDefault();

    UserAddress getById(Integer addressId);

    /**
     * 获取地址详情
     * @param id 地址id
     * @return UserAddress
     */
    UserAddress getDetail(Integer id);

    /**
     * 获取默认地址
     * @return UserAddress
     */
    UserAddress getDefaultByUid(Integer uid);
}
