package com.hdkj.mall.export.service;

import com.hdkj.mall.article.model.ActivitySign;
import com.hdkj.mall.bargain.request.StoreBargainSearchRequest;
import com.hdkj.mall.combination.request.StoreCombinationSearchRequest;
import com.hdkj.mall.store.request.StoreProductSearchRequest;

import javax.servlet.http.HttpServletResponse;

/**
* StoreProductService 接口
*/
public interface ExcelService{

    String exportBargainProduct(StoreBargainSearchRequest request, HttpServletResponse response);

    String exportCombinationProduct(StoreCombinationSearchRequest request, HttpServletResponse response);

    String exportProduct(StoreProductSearchRequest request, HttpServletResponse response);

    String exportActivityUser(ActivitySign activitySign, HttpServletResponse response);
}
