package com.hdkj.mall.front.service;

import com.common.CommonPage;
import com.common.PageParamRequest;
import com.hdkj.mall.front.response.IndexInfoResponse;
import com.hdkj.mall.front.response.IndexProductBannerResponse;
import com.hdkj.mall.front.response.IndexProductResponse;

import java.util.HashMap;
import java.util.List;

/**
* IndexService 接口
*/
public interface IndexService{

    IndexProductBannerResponse getProductBanner(int type, PageParamRequest pageParamRequest);

    /**
     * 首页信息
     * @return IndexInfoResponse
     */
    IndexInfoResponse getIndexInfo();

    /**
     * 首页信息
     * @return IndexInfoResponse
     */
    IndexInfoResponse getHomeData();

    HashMap<String, Object> getServiceData(Integer type);

    List<HashMap<String, Object>> hotKeywords();

    HashMap<String, String> getShareConfig();

    /**
     * 获取首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return List
     */
    CommonPage<IndexProductResponse> findIndexProductList(Integer type, PageParamRequest pageParamRequest);
}
