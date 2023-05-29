package com.hdkj.mall.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.article.model.Activity;
import com.hdkj.mall.article.model.ActivityCollect;
import com.hdkj.mall.article.request.ActivityRequest;
import com.hdkj.mall.article.model.ActivitySign;
import com.hdkj.mall.article.vo.ActivityVo;

import java.util.List;

/**
*  ActivityService 接口
*/
public interface ActivityService extends IService<Activity> {

    PageInfo<ActivityVo> getAdminList(ActivityRequest request, PageParamRequest pageParamRequest);

    /**
     * 文章详情
     * @param id 文章id
     * @return ArticleVo
     */
    ActivityVo getVoByFront(Integer id);
    /**
     * 分类文章详情
     * @param cid 分类ID
     * @return ArticleVo
     */
    List<ActivityVo> getVoByHotFront(Integer cid);


    /**
     * 添加活动
     * @return List<Categoy>
     */
    Boolean saveActivity(Activity activity);

    /**
     * 活动报名
     */
    Boolean activitySign(ActivitySign activitySign);

    /**
     * 取消报名
     */
    Boolean activitySign_qx(ActivitySign activitySign);
    /**
     * 活动收藏
     */
    Boolean activityCollect(ActivityCollect activityCollect);

    /**
     * 取消活动收藏
     */
    Boolean activitySign_qx(ActivityCollect activityCollect);


    /**
     * 报名列表
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<ActivitySign> getlistActivitySign(ActivitySign request, PageParamRequest pageParamRequest);

    /**
     * 活动列表
     */
    PageInfo<ActivityVo> getlistActicity(Integer typer,Integer type,Integer pid,String content, PageParamRequest pageParamRequest);
    /**
     * 用户活动列表
     */
    PageInfo<ActivityVo> getlistActicityByUid(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 报名详情
     * @param id 文章id
     * @return ArticleVo
     */
    ActivitySign getActivityBm(Integer id);


    /**
     * 报名列表
     * @param did
     */
    List<ActivitySign> getListActivityBm(Integer did);

}
