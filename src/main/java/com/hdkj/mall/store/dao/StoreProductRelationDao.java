package com.hdkj.mall.store.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hdkj.mall.front.response.UserRelationResponse;
import com.hdkj.mall.store.model.StoreProductRelation;
import com.hdkj.mall.store.vo.StoreProductRelationCountVo;

import java.util.HashMap;
import java.util.List;

/**
 * 商品点赞和收藏表 Mapper 接口
 */
public interface StoreProductRelationDao extends BaseMapper<StoreProductRelation> {

    List<StoreProductRelationCountVo> getCountInProductId(HashMap<String, Object> objectObjectHashMap);

    List<UserRelationResponse> getUserList(Integer uid);
}
