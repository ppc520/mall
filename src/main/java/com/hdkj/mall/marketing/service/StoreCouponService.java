package com.hdkj.mall.marketing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.common.SearchAndPageRequest;
import com.hdkj.mall.marketing.model.StoreCoupon;
import com.hdkj.mall.marketing.request.StoreCouponRequest;
import com.hdkj.mall.marketing.request.StoreCouponSearchRequest;
import com.hdkj.mall.marketing.response.StoreCouponFrontResponse;
import com.hdkj.mall.marketing.response.StoreCouponInfoResponse;

import java.util.List;

/**
 * StoreCouponService 接口
 */
public interface StoreCouponService extends IService<StoreCoupon> {

    List<StoreCoupon> getList(StoreCouponSearchRequest request, PageParamRequest pageParamRequest);

    boolean create(StoreCouponRequest request);

    StoreCoupon getInfoException(Integer id);

    void checkException(StoreCoupon storeCoupon);

    StoreCouponInfoResponse info(Integer id);

    /**
     * 根据优惠券id获取
     * @param ids 优惠券id集合
     * @return 优惠券列表
     */
    List<StoreCoupon> getByIds(List<Integer> ids);

    /**
     * 扣减数量
     * @param id 优惠券id
     * @param num 数量
     * @param isLimited 是否限量
     */
    Boolean deduction(Integer id, Integer num, Boolean isLimited);

    /**
     * 获取用户注册赠送新人券
     * @return 优惠券列表
     */
    List<StoreCoupon> findRegisterList();

    /**
     * 发送优惠券列表
     * @param searchAndPageRequest 搜索分页参数
     * @return 优惠券列表
     */
    List<StoreCoupon> getSendList(SearchAndPageRequest searchAndPageRequest);

    /**
     * 删除优惠券
     * @param id 优惠券id
     * @return Boolean
     */
    Boolean delete(Integer id);

    /**
     * 获取首页优惠券列表（最多两条,不够两条不返回）
     */
    List<StoreCoupon> getHomeIndexCoupon();

    /**
     * 移动端优惠券列表
     * @param type 类型，1-通用，2-商品，3-品类
     * @param productId 产品id，搜索产品指定优惠券
     * @param pageParamRequest 分页参数
     * @return List<StoreCouponFrontResponse>
     */
    List<StoreCouponFrontResponse> getH5List(Integer type, Integer productId, PageParamRequest pageParamRequest);
}
