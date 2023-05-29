package com.hdkj.mall.article.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.exception.MallException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.article.dao.*;
import com.hdkj.mall.article.model.Activity;
import com.hdkj.mall.article.model.ActivityCollect;
import com.hdkj.mall.article.model.ActivitySign;
import com.hdkj.mall.article.request.ActivityRequest;
import com.hdkj.mall.article.service.ActivityService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.hdkj.mall.user.model.User;
import com.utils.MallUtil;
import com.hdkj.mall.article.vo.ActivityVo;
import com.hdkj.mall.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* ActivityServiceImpl 接口实现
*/
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityDao, Activity> implements ActivityService {

    private Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);

    @Resource
    private ActivityDao dao;
    @Resource
    private ActivitySignDao activitySignDao;
    @Resource
    private ActivityCollectDao activityCollectDao;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Resource
    private DynamicPraiseDao dynamicPraiseDao;
    @Resource
    private ArticleCommentDao articleCommentDao;

    @Override
    public PageInfo<ActivityVo> getAdminList(ActivityRequest request, PageParamRequest pageParamRequest) {
        Page<Activity> activityPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Activity> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if(request.getCid() !=null && request.getCid()!=0){
            lambdaQueryWrapper.eq(Activity::getCid, request.getCid());
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.like(Activity::getTitle, request.getKeywords());
        }

        lambdaQueryWrapper.orderByDesc(Activity::getIsTop).orderByDesc(Activity::getIsHot).orderByDesc(Activity::getSort).orderByDesc(Activity::getVisit).orderByDesc(Activity::getReview).orderByDesc(Activity::getCreateTime);
        List<Activity> activityList = dao.selectList(lambdaQueryWrapper);

        ArrayList<ActivityVo> activityVoArrayList = new ArrayList<>();
        if(activityList.size() < 1){
            return CommonPage.copyPageInfo(activityPage, activityVoArrayList);
        }

        for (Activity activity : activityList) {
            ActivityVo activityVo = new ActivityVo();

            BeanUtils.copyProperties(activity, activityVo);
            if(!StringUtils.isBlank(activity.getSliderImage()) ){
                activityVo.setSliderImages(MallUtil.jsonToListString(activity.getSliderImage()));
                activityVo.setSliderImage(activity.getSliderImage());
            }
            User user = userService.getById(activityVo.getUid());
            if(user!=null){
                activityVo.setUname(user.getNickname());
            }
            Category category = categoryService.getById(activityVo.getPid());
            if(category!=null){
                activityVo.setPname(category.getName());
            }
            category = categoryService.getById(activityVo.getCid());
            if(category!=null){
                activityVo.setCname(category.getName());
            }
            activityVoArrayList.add(activityVo);
        }

        return CommonPage.copyPageInfo(activityPage, activityVoArrayList);
    }


    /**
     * 查询动态详情
     * @param id Integer
     */
    @Override
    public ActivityVo getVoByFront(Integer id) {
        Activity activity = getById(id);
        if (ObjectUtil.isNull(activity)) {
            throw new MallException("活动不存在");
        }
        if(activity.getStatus()){
            throw new MallException("活动不存在");
        }

        ActivityVo activityVo = new ActivityVo();
        BeanUtils.copyProperties(activity, activityVo);
//        try {
//            if(dynamic.getVisit()==null){
//                dynamic.setVisit(0);
//            }
//            dynamic.setVisit(dynamic.getVisit()+1);
//            dao.updateById(dynamic);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("查看动态详情，更新浏览量失败，errorMsg = " + e.getMessage());
//        }
        return activityVo;
    }

    /**
     * 话题动态
     * @param cid 分类ID
     */
    @Override
    public List<ActivityVo>  getVoByHotFront(Integer cid) {
        LambdaQueryWrapper<Activity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Activity::getCid,cid);
        lambdaQueryWrapper.orderByDesc(Activity::getIsTop).orderByDesc(Activity::getIsHot).orderByDesc(Activity::getSort).orderByDesc(Activity::getVisit).orderByDesc(Activity::getReview).orderByDesc(Activity::getUpdateTime);
        List<Activity> list = dao.selectList(lambdaQueryWrapper);

        if(list ==null|| list.size()==0){
            throw new MallException("该话题没有动态");
        }
        ArrayList<ActivityVo> activityVoArrayList = new ArrayList<>();
        for (Activity activity : list) {
            ActivityVo activityVo = new ActivityVo();

            BeanUtils.copyProperties(activity, activityVo);
            if(!StringUtils.isBlank(activity.getSliderImage()) ){
                activityVo.setSliderImages(MallUtil.jsonToListString(activity.getSliderImage()));
                activityVo.setSliderImage(activity.getSliderImage());
            }
            User user = userService.getById(activityVo.getUid());
            if(user!=null){
                activityVo.setUname(user.getNickname());
            }
            Category category = categoryService.getById(activityVo.getPid());
            if(category!=null){
                activityVo.setPname(category.getName());
            }
            category = categoryService.getById(activityVo.getCid());
            if(category!=null){
                activityVo.setCname(category.getName());
            }
            activityVoArrayList.add(activityVo);
        }
        return activityVoArrayList;
    }

    /**
     * 添加活动
     * @param activity
     */
    @Override
    public Boolean saveActivity(Activity activity) {
        Boolean b = false;
        try {
            if(save(activity)){
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加活动失败，errorMsg = " + e.getMessage());
        }
        return b;
    }

    /**
     * 活动报名
     * @param activitySign
     */
    @Override
    public Boolean activitySign(ActivitySign activitySign) {
        Boolean b = false;
        try {
            if(activitySignDao.insert(activitySign)>0){
                Activity activity = getById(activitySign.getDid());
                if(activity !=null){
                    if(activity.getReview()==null){
                        activity.setReview(0);
                    }
                    activity.setReview(activity.getReview()+1);
                    dao.updateById(activity);
                }
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("活动报名失败，更新报名量失败，errorMsg = " + e.getMessage());
        }
        return b;
    }

    /**
     * 取消报名
     * @param activitySign
     */
    @Override
    public Boolean activitySign_qx(ActivitySign activitySign) {
        Boolean b = false;
        try {
            LambdaQueryWrapper<ActivitySign> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(ActivitySign::getUid,activitySign.getUid());
            lambdaQueryWrapper.eq(ActivitySign::getDid,activitySign.getDid());
            if(activitySignDao.delete(lambdaQueryWrapper)>0){
                Activity activity = getById(activitySign.getDid());
                if(activity !=null){
                    if(activity.getReview()==null){
                        activity.setReview(0);
                    }else{
                        activity.setReview(activity.getReview()-1);
                    }
                    dao.updateById(activity);
                }
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("取消活动报名失败，更新报名量失败，errorMsg = " + e.getMessage());
        }
        return b;
    }
    /**
     * 活动报名
     * @param activityCollect
     */
    @Override
    public Boolean activityCollect(ActivityCollect activityCollect) {
        Boolean b = false;
        try {
            if(activityCollectDao.insert(activityCollect)>0){
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("活动收藏失败，errorMsg = " + e.getMessage());
        }
        return b;
    }

    /**
     * 取消活动收藏
     * @param activityCollect
     */
    @Override
    public Boolean activitySign_qx(ActivityCollect activityCollect) {
        Boolean b = false;
        try {
            LambdaQueryWrapper<ActivityCollect> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(ActivityCollect::getUid,activityCollect.getUid());
            lambdaQueryWrapper.eq(ActivityCollect::getDid,activityCollect.getDid());
            if(activityCollectDao.delete(lambdaQueryWrapper)>0){
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("取消活动报名失败，更新报名量失败，errorMsg = " + e.getMessage());
        }
        return b;
    }

    @Override
    public PageInfo<ActivitySign> getlistActivitySign(ActivitySign request, PageParamRequest pageParamRequest) {
        Page<ActivitySign> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<ActivitySign> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ActivitySign::getDid,request.getDid());
        if(request.getName() !=null && !request.getName().equals("")){
            lambdaQueryWrapper.and(i -> i.or().like(ActivitySign::getName, request.getName())
                    .or().like(ActivitySign::getContact, request.getName()));
        }
        lambdaQueryWrapper.orderByDesc(ActivitySign::getCreateTime);
        List<ActivitySign> activitySignList = activitySignDao.selectList(lambdaQueryWrapper);
        return CommonPage.copyPageInfo(dynamicPage, activitySignList);
    }

    @Override
    public List<ActivitySign> getListActivityBm(Integer did) {
        LambdaQueryWrapper<ActivitySign> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ActivitySign::getDid,did);
        lambdaQueryWrapper.orderByDesc(ActivitySign::getCreateTime);
        return  activitySignDao.selectList(lambdaQueryWrapper);
    }


    /**
     * @param typer (0-普通查询，1-热门活动，2-我收藏的，3-我报名的)
     * @param pageParamRequest
     * @return
     */
    @Override
    public PageInfo<ActivityVo> getlistActicity(Integer typer,Integer type,Integer pid, String content,PageParamRequest pageParamRequest) {

        Page<Activity> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Activity> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Activity::getStatus,false);
        if(typer==0){
            lambdaQueryWrapper.eq(Activity::getCid,type);
            if(pid !=null && pid>0){
                lambdaQueryWrapper.and(i -> i.or().eq(Activity::getPid, pid)
                        .or().eq(Activity::getPid, 0)
                );
            }
            if(!StringUtils.isBlank(content)){
                lambdaQueryWrapper.and(i -> i.or().like(Activity::getTitle, content)
                        .or().like(Activity::getAuthor, content)
                );
            }
        }else if(typer==1){
            lambdaQueryWrapper.eq(Activity::getIsHot,true);
        }else if(typer==2){

            User user = userService.getInfo();
            if(user !=null){
                LambdaQueryWrapper<ActivityCollect> lambdaQuery = Wrappers.lambdaQuery();
                lambdaQuery.eq(ActivityCollect::getUid,user.getUid());
                lambdaQuery.orderByDesc(ActivityCollect::getCreateTime);
                List<ActivityCollect> list = activityCollectDao.selectList(lambdaQuery);
                if(list !=null && list.size()>0){
                    List<Integer> list1 = new ArrayList<>();
                    for(ActivityCollect activityCollect :list){
                        list1.add(activityCollect.getDid());
                    }
                    lambdaQueryWrapper.in(Activity::getId,list1);
                }else{
                    lambdaQueryWrapper.eq(Activity::getId,0);
                }
            }else{
                lambdaQueryWrapper.eq(Activity::getId,0);
            }
        }else if(typer==3){

        }

        lambdaQueryWrapper.eq(Activity::getIsCheck,1);
        lambdaQueryWrapper.orderByDesc(Activity::getCreateTime);
        List<Activity> list = dao.selectList(lambdaQueryWrapper);
        if(list!=null && list.size()>0){
            List<ActivityVo> activityVoArrayList = new ArrayList<>();
            for (Activity activity : list){
                ActivityVo activityVo = new ActivityVo();
                BeanUtils.copyProperties(activity, activityVo);

                if(!StringUtils.isBlank(activity.getSliderImage()) ){
                    activityVo.setSliderImages(MallUtil.jsonToListString(activity.getSliderImage()));
                }
                if(activity.getType() ==0){
                    if(activityVo.getSignStart().before(new Date())){
                        activityVo.setIsKSBm(true);
                    }else{
                        activityVo.setIsKSBm(false);
                    }
                    if(activityVo.getSignEnd().before(new Date())){
                        activityVo.setIsBmGQ(true);
                    }else{
                        activityVo.setIsBmGQ(false);
                    }
                    if(activityVo.getActivityEnd().before(new Date())){
                        activityVo.setIsHdGQ(true);
                    }else{
                        activityVo.setIsHdGQ(false);
                    }
                }

                if(activity.getUid()!=null){
                    Category category = categoryService.getById(activity.getUid());
                    if(category!=null){
                        activityVo.setUname(category.getName());
                    }
                }
                Category category = categoryService.getById(activity.getPid());
                if(category!=null){
                    activityVo.setPname(category.getName());
                }
                category = categoryService.getById(activity.getCid());
                if(category!=null){
                    activityVo.setCname(category.getName());
                }
                User user = userService.getInfo();
                if(user==null){
                    activityVo.setIsBm(false);
//                    activityVo.setIsSc(false);
//                    activityVo.setIsZan(false);
                }else{
                    LambdaQueryWrapper<ActivitySign> lambdaQueryWrapper1 = Wrappers.lambdaQuery();
                    lambdaQueryWrapper1.eq(ActivitySign::getUid,user.getUid());
                    lambdaQueryWrapper1.eq(ActivitySign::getDid,activityVo.getId());
                    if(activitySignDao.selectOne(lambdaQueryWrapper1)==null){
                        activityVo.setIsBm(false);
                    }else{
                        activityVo.setIsBm(true);
                    }

//                    LambdaQueryWrapper<ActivityCollect> lambdaQueryWrapper2 = Wrappers.lambdaQuery();
//                    lambdaQueryWrapper2.eq(ActivityCollect::getUid,user.getUid());
//                    lambdaQueryWrapper2.eq(ActivityCollect::getDid,activityVo.getId());
//                    if(activityCollectDao.selectOne(lambdaQueryWrapper2)==null){
//                        activityVo.setIsSc(false);
//                    }else{
//                        activityVo.setIsSc(true);
//                    }

//                    LambdaQueryWrapper<DynamicPraise> lambdaQuery = Wrappers.lambdaQuery();
//                    lambdaQuery.eq(DynamicPraise::getUid, user.getUid());
//                    lambdaQuery.eq(DynamicPraise::getDid, activity.getId());
//                    lambdaQuery.eq(DynamicPraise::getType, 5);
//                    if(dynamicPraiseDao.selectOne(lambdaQuery)==null){
//                        activityVo.setIsZan(false);
//                    }else{
//                        activityVo.setIsZan(true);
//                    }
                }
//
//                LambdaQueryWrapper<ArticleComments> lambdaQuery = Wrappers.lambdaQuery();
//                lambdaQuery.eq(ArticleComments::getArtid,activityVo.getId());
//                lambdaQuery.eq(ArticleComments::getType,2);
//                activityVo.setPl(articleCommentDao.selectCount(lambdaQuery));

                activityVoArrayList.add(activityVo);
            }
            return CommonPage.copyPageInfo(dynamicPage, activityVoArrayList);
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }
    /**
     * @param pageParamRequest
     * @return
     */
    @Override
    public PageInfo<ActivityVo> getlistActicityByUid(Integer uid, PageParamRequest pageParamRequest) {
        if(uid==null || uid ==0){
            throw new MallException("参数错误，获取失败");
        }
        Page<Activity> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<ActivitySign> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(ActivitySign::getUid,uid);
        List<ActivitySign> activitySignList = activitySignDao.selectList(lambdaQuery);
        if(activitySignList!=null && activitySignList.size()>0){
            List<Integer> list1 = new ArrayList<>();
            for(ActivitySign activitySign :activitySignList){
                list1.add(activitySign.getDid());
            }

            LambdaQueryWrapper<Activity> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(Activity::getStatus,false);
//            lambdaQueryWrapper.eq(Activity::getUid,uid);
            lambdaQueryWrapper.in(Activity::getId,list1);
            lambdaQueryWrapper.eq(Activity::getIsCheck,1);
            lambdaQueryWrapper.orderByDesc(Activity::getCreateTime);
            List<Activity> list = dao.selectList(lambdaQueryWrapper);
            if(list!=null && list.size()>0){
                List<ActivityVo> activityVoArrayList = new ArrayList<>();
                for (Activity activity : list){
                    ActivityVo activityVo = new ActivityVo();
                    BeanUtils.copyProperties(activity, activityVo);

                    if(!StringUtils.isBlank(activity.getSliderImage()) ){
                        activityVo.setSliderImages(MallUtil.jsonToListString(activity.getSliderImage()));
                    }
                    if(activityVo.getSignStart().before(new Date())){
                        activityVo.setIsKSBm(true);
                    }else{
                        activityVo.setIsKSBm(false);
                    }
                    if(activityVo.getSignEnd().before(new Date())){
                        activityVo.setIsBmGQ(true);
                    }else{
                        activityVo.setIsBmGQ(false);
                    }
                    if(activityVo.getActivityEnd().before(new Date())){
                        activityVo.setIsHdGQ(true);
                    }else{
                        activityVo.setIsHdGQ(false);
                    }

                    if(activity.getUid()!=null){
                        Category category = categoryService.getById(activity.getUid());
                        if(category!=null){
                            activityVo.setUname(category.getName());
                        }
                    }
                    Category category = categoryService.getById(activity.getPid());
                    if(category!=null){
                        activityVo.setPname(category.getName());
                    }
                    category = categoryService.getById(activity.getCid());
                    if(category!=null){
                        activityVo.setCname(category.getName());
                    }

                User user = userService.getInfo();
                if(user==null){
                    activityVo.setIsBm(false);
//                    activityVo.setIsSc(false);
//                    activityVo.setIsZan(false);
                }else{
                    LambdaQueryWrapper<ActivitySign> lambdaQueryWrapper1 = Wrappers.lambdaQuery();
                    lambdaQueryWrapper1.eq(ActivitySign::getUid,user.getUid());
                    lambdaQueryWrapper1.eq(ActivitySign::getDid,activityVo.getId());
                    if(activitySignDao.selectOne(lambdaQueryWrapper1)==null){
                        activityVo.setIsBm(false);
                    }else{
                        activityVo.setIsBm(true);
                    }

//                    LambdaQueryWrapper<ActivityCollect> lambdaQueryWrapper2 = Wrappers.lambdaQuery();
//                    lambdaQueryWrapper2.eq(ActivityCollect::getUid,user.getUid());
//                    lambdaQueryWrapper2.eq(ActivityCollect::getDid,activityVo.getId());
//                    if(activityCollectDao.selectOne(lambdaQueryWrapper2)==null){
//                        activityVo.setIsSc(false);
//                    }else{
//                        activityVo.setIsSc(true);
//                    }

//                    LambdaQueryWrapper<DynamicPraise> lambdaQuery = Wrappers.lambdaQuery();
//                    lambdaQuery.eq(DynamicPraise::getUid, user.getUid());
//                    lambdaQuery.eq(DynamicPraise::getDid, activity.getId());
//                    lambdaQuery.eq(DynamicPraise::getType, 5);
//                    if(dynamicPraiseDao.selectOne(lambdaQuery)==null){
//                        activityVo.setIsZan(false);
//                    }else{
//                        activityVo.setIsZan(true);
//                    }
                }
//
//                LambdaQueryWrapper<ArticleComments> lambdaQuery = Wrappers.lambdaQuery();
//                lambdaQuery.eq(ArticleComments::getArtid,activityVo.getId());
//                lambdaQuery.eq(ArticleComments::getType,2);
//                activityVo.setPl(articleCommentDao.selectCount(lambdaQuery));

                    activityVoArrayList.add(activityVo);
                }
                return CommonPage.copyPageInfo(dynamicPage, activityVoArrayList);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }
    /**
     * 报名详情
     * @param id Integer
     */
    @Override
    public ActivitySign getActivityBm(Integer id) {
        return activitySignDao.selectById(id);
    }

}

