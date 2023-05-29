package com.hdkj.mall.front.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.SysConfigConstants;
import com.exception.MallException;
import com.hdkj.mall.article.service.ArticleService;
import com.hdkj.mall.front.response.IndexInfoResponse;
import com.hdkj.mall.front.response.IndexProductBannerResponse;
import com.hdkj.mall.front.response.IndexProductResponse;
import com.hdkj.mall.front.service.IndexService;
import com.hdkj.mall.front.service.ProductService;
import com.hdkj.mall.marketing.model.StoreCoupon;
import com.hdkj.mall.marketing.model.StoreCouponUser;
import com.hdkj.mall.marketing.service.StoreCouponService;
import com.hdkj.mall.store.service.StoreProductService;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.system.service.SystemGroupDataService;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.service.UserService;
import com.hdkj.mall.marketing.service.StoreCouponUserService;
import com.utils.MallUtil;
import com.hdkj.mall.front.request.IndexStoreProductSearchRequest;
import com.hdkj.mall.front.response.ProductActivityItemResponse;
import com.hdkj.mall.store.model.StoreProduct;
import com.hdkj.mall.store.utilService.ProductUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* IndexServiceImpl 接口实现
*/
@Service
public class IndexServiceImpl implements IndexService {

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private StoreCouponService storeCouponService;

    @Autowired
    private StoreProductService storeProductService;

    @Autowired
    private ProductUtils productUtils;

    @Autowired
    private StoreCouponUserService storeCouponUserService;

    @Autowired
    private ArticleService articleService;

    /**
     * 首页产品的轮播图和产品信息
     * @param type integer 类型
     * @return HashMap<String, Object>
     */
    @Override
    public IndexProductBannerResponse getProductBanner(int type, PageParamRequest pageParamRequest) {

        IndexProductBannerResponse indexProductBannerResponse = new IndexProductBannerResponse();
        IndexStoreProductSearchRequest request = new IndexStoreProductSearchRequest();
        int gid;
        String key;
        switch (type){
            case Constants.INDEX_RECOMMEND_BANNER: //精品推荐
                gid = Constants.GROUP_DATA_ID_INDEX_RECOMMEND_BANNER;
                key = Constants.INDEX_BAST_LIMIT;
                request.setIsBest(true);
                break;
            case Constants.INDEX_HOT_BANNER: //热门榜单
                gid = Constants.GROUP_DATA_ID_INDEX_HOT_BANNER;
                key = Constants.INDEX_HOT_LIMIT;
                request.setIsHot(true);
                break;
            case Constants.INDEX_NEW_BANNER: //首发新品
                gid = Constants.GROUP_DATA_ID_INDEX_NEW_BANNER;
                key = Constants.INDEX_FIRST_LIMIT;
                request.setIsNew(true);
                break;
            case Constants.INDEX_BENEFIT_BANNER: //促销单品
                gid = Constants.GROUP_DATA_ID_INDEX_BENEFIT_BANNER;
                key = Constants.INDEX_SALES_LIMIT;
                request.setIsBenefit(true);
                break;
            default:
                return null;
        }

        if(StringUtils.isNotBlank(key)){
            String num = systemConfigService.getValueByKey(Constants.INDEX_BAST_LIMIT);
            if(pageParamRequest.getLimit() == 0){
                //首页limit传0，则读取默认数据， 否则后台设置的首页配置不起作用
                pageParamRequest.setLimit(Integer.parseInt(num));
            }
        }

        indexProductBannerResponse.setBanner(systemGroupDataService.getListMapByGid(gid));
        indexProductBannerResponse.setList(productService.getIndexProduct(request, pageParamRequest).getList());
        return indexProductBannerResponse;
    }

    /**
     * 首页数据
     * banner、金刚区、广告位
     */
    @Override
    public IndexInfoResponse getIndexInfo() {
        IndexInfoResponse indexInfoResponse = new IndexInfoResponse();
        indexInfoResponse.setBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_BANNER)); //首页banner滚动图
        indexInfoResponse.setMenus(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_MENU)); //导航模块
        indexInfoResponse.setRoll(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER)); //首页滚动新闻

        indexInfoResponse.setLogoUrl(systemConfigService.getValueByKey(Constants.CONFIG_KEY_SITE_LOGO));// 企业logo地址
        indexInfoResponse.setYzfUrl(systemConfigService.getValueByKey(Constants.CONFIG_KEY_YZF_H5_URL));// 云智服H5 url
        indexInfoResponse.setSubscribe(false);
        User user = userService.getInfo();
        if(ObjectUtil.isNotNull(user) && user.getSubscribe()){
            indexInfoResponse.setSubscribe(user.getSubscribe());
        }
        // 获取首页优惠券列表
        List<StoreCoupon> couponList = storeCouponService.getHomeIndexCoupon();
        //获取用户当前已领取未使用的优惠券
        if (ObjectUtil.isNotNull(user) && CollUtil.isNotEmpty(couponList)) {
            HashMap<Integer, StoreCouponUser> couponUserMap = storeCouponUserService.getMapByUserId(user.getUid());
            for (StoreCoupon storeCoupon : couponList) {
                if (CollUtil.isNotEmpty(couponUserMap) && couponUserMap.containsKey(storeCoupon.getId())) {
                    storeCoupon.setIsGet(true);
                }
            }
        }
        indexInfoResponse.setCouponList(couponList);

        indexInfoResponse.setExplosiveMoney(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_EX_BANNER));//首页超值爆款
        indexInfoResponse.setBastBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_BEST_BANNER)); //首页精品推荐图片
        return indexInfoResponse;
    }

    /**
     * 热门搜索
     * @return List<HashMap<String, String>>
     */
    @Override
    public List<HashMap<String, Object>> hotKeywords() {
        return systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_KEYWORDS);
    }

    /**
     * 微信分享配置
     * @return Object
     */
    @Override
    public HashMap<String, String> getShareConfig() {
        HashMap<String, String> map = new HashMap<>();
        HashMap<String, String> info = systemConfigService.info(Constants.CONFIG_FORM_ID_PUBLIC);
        if(info == null){
            throw new MallException("请配置公众号分享信息！");
        }
        map.put("img", info.get(SysConfigConstants.CONFIG_KEY_ADMIN_WECHAT_SHARE_IMAGE));
        map.put("title", info.get(SysConfigConstants.CONFIG_KEY_ADMIN_WECHAT_SHARE_TITLE));
        map.put("synopsis", info.get(SysConfigConstants.CONFIG_KEY_ADMIN_WECHAT_SHARE_SYNOSIS));
        return map;
    }

    /**
     * 获取首页商品列表
     * @param type 类型 【1 精品推荐 2 热门榜单 3首发新品 4促销单品】
     * @param pageParamRequest 分页参数
     * @return List
     */
    @Override
    public CommonPage<IndexProductResponse> findIndexProductList(Integer type, PageParamRequest pageParamRequest) {
        List<StoreProduct> storeProductList = storeProductService.getIndexProduct(type, pageParamRequest);
        if(CollUtil.isEmpty(storeProductList)){
            return CommonPage.restPage(new ArrayList<>());
        }
        CommonPage<StoreProduct> storeProductCommonPage = CommonPage.restPage(storeProductList);

        List<IndexProductResponse> productResponseArrayList = new ArrayList<>();
        for (StoreProduct storeProduct : storeProductList) {
            IndexProductResponse productResponse = new IndexProductResponse();
            List<Integer> activityList = MallUtil.stringToArrayInt(storeProduct.getActivity());
            // 活动类型默认：直接跳过
            if (activityList.get(0).equals(Constants.PRODUCT_TYPE_NORMAL)) {
                BeanUtils.copyProperties(storeProduct, productResponse);
                productResponseArrayList.add(productResponse);
                continue;
            }
            // 根据参与活动添加对应商品活动标示
            HashMap<Integer, ProductActivityItemResponse> activityByProduct =
                    productUtils.getActivityByProduct(storeProduct.getId(), storeProduct.getActivity());
            if (CollUtil.isNotEmpty(activityByProduct)) {
                for (Integer activity : activityList) {
                    if (activity.equals(Constants.PRODUCT_TYPE_NORMAL)) {
                        break;
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_SECKILL)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_SECKILL);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_BARGAIN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_BARGAIN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                    if (activity.equals(Constants.PRODUCT_TYPE_PINGTUAN)) {
                        ProductActivityItemResponse itemResponse = activityByProduct.get(Constants.PRODUCT_TYPE_PINGTUAN);
                        if (ObjectUtil.isNotNull(itemResponse)) {
                            productResponse.setActivityH5(itemResponse);
                            break;
                        }
                    }
                }
            }
            BeanUtils.copyProperties(storeProduct, productResponse);
            productResponseArrayList.add(productResponse);
        }
        CommonPage<IndexProductResponse> productResponseCommonPage = CommonPage.restPage(productResponseArrayList);
        BeanUtils.copyProperties(storeProductCommonPage, productResponseCommonPage, "list");
        return productResponseCommonPage;
    }

    /**
     * 首页数据
     * banner、金刚区、广告位
     */
    @Override
    public IndexInfoResponse getHomeData() {
        IndexInfoResponse indexInfoResponse = new IndexInfoResponse();
        indexInfoResponse.setActivityBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_HOME_ACTICITY__BANNER)); //移动端_新首页_活动广告图
        indexInfoResponse.setBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_HOME_BANNER)); //首页banner滚动图
        indexInfoResponse.setMenus(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_HOME_SEVICE_BANNER)); //新-首页服务菜单
//        indexInfoResponse.setBastBanner(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_HOME_BOTTOM_BANNER)); //首页底部推荐图片
        indexInfoResponse.setSubscribe(false);
        indexInfoResponse.setListCategory(articleService.getCategoryList());
        return indexInfoResponse;
    }

    @Override
    public HashMap<String, Object> getServiceData(Integer type) {
        HashMap<String, Object> map = new HashMap<>();
        if(type==0){
            map.put("menus",systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_LAUNDRY_MENUS));
            map.put("banners",systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_LAUNDRY_BANNER));
            return map;
        }else if(type==1){
            map.put("menus",systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_RECYCLE_MENUS));
            map.put("banners",systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_RECYCLE__BANNER));
            return map;
        }else if(type ==2){
            map.put("menus",systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_MAINTAIN_MENUS));
            map.put("banners",systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_MAINTAIN_BANNER));
            return map;
        }else{
            throw new MallException("获取失败！");
        }
    }
}

