package com.hdkj.mall.article.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.MallException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.article.dao.*;
import com.hdkj.mall.article.model.*;
import com.hdkj.mall.article.service.DynamicService;
import com.hdkj.mall.article.vo.*;
import com.hdkj.mall.system.service.SystemGroupDataService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.hdkj.mall.front.response.MessageInfoResponse;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.user.model.User;
import com.utils.MallUtil;
import com.hdkj.mall.article.request.DynamicRequest;
import com.hdkj.mall.article.service.ActivityService;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.constants.Constants.ARTICLE_BANNER_LIMIT;

/**
* DynamicServiceImpl 接口实现
*/
@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicDao, Dynamic> implements DynamicService {

    private Logger logger = LoggerFactory.getLogger(DynamicServiceImpl.class);

    @Resource
    private DynamicDao dao;

    @Resource
    private DynamicCommentsDao dynamicCommentsDao;

    @Resource
    private UserAttentionDao userAttentionDao;

    @Resource
    private DynamicPraiseDao dynamicPraiseDao;

    @Resource
    private ActivitySignDao activitySignDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private DynamicService dynamicService;

    @Override
    public PageInfo<DynamicVo> getAdminList(DynamicRequest request, PageParamRequest pageParamRequest) {
        Page<Dynamic> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if(request.getCid() !=null && request.getCid()!=0){
            lambdaQueryWrapper.eq(Dynamic::getCid, request.getCid());
        }
        if(request.getType()>-1){
            lambdaQueryWrapper.eq(Dynamic::getType, request.getType());
        }

        if(request.getPid()!=null && request.getPid()!=0){
            lambdaQueryWrapper.eq(Dynamic::getPid, request.getPid());
        }

        if(request.getIsTop()!=null){
            lambdaQueryWrapper.eq(Dynamic::getIsTop, request.getIsTop());
        }

        if(request.getIsHot()!=null){
            lambdaQueryWrapper.eq(Dynamic::getIsHot, request.getIsHot());
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.or().like(Dynamic::getTitle, request.getKeywords())
                    .or().like(Dynamic::getSynopsis, request.getKeywords())
                    .or().like(Dynamic::getContent, request.getKeywords()));
        }

        lambdaQueryWrapper.orderByDesc(Dynamic::getIsTop)
                .orderByDesc(Dynamic::getIsHot)
                .orderByDesc(Dynamic::getSort)
                .orderByDesc(Dynamic::getCreateTime);
        List<Dynamic> dynamicList = dao.selectList(lambdaQueryWrapper);

        ArrayList<DynamicVo> dynamicVoArrayList = new ArrayList<>();
        if(dynamicList.size() < 1){
            return CommonPage.copyPageInfo(dynamicPage, dynamicVoArrayList);
        }

        for (Dynamic dynamic : dynamicList) {
            DynamicVo dynamicVo = new DynamicVo();

            BeanUtils.copyProperties(dynamic, dynamicVo);
            if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                dynamicVo.setSliderImage(dynamic.getSliderImage());
            }

            if(dynamicVo.getAdminId() ==0){
                User user = userService.getById(dynamicVo.getUid());
                if(user!=null){
                    dynamicVo.setUname(user.getNickname());
                }
            }else{
                Category category = categoryService.getById(dynamicVo.getUid());
                if(category!=null){
                    dynamicVo.setUname(category.getName());
                    dynamicVo.setImg(category.getExtra());
                }
            }

            Category category = categoryService.getById(dynamicVo.getPid());
            if(category!=null){
                dynamicVo.setPname(category.getName());
            }
            if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                category = categoryService.getById(dynamicVo.getCid());
                if(category!=null){
                    dynamicVo.setCname(category.getName());
                }
            }
            dynamicVoArrayList.add(dynamicVo);
        }

        return CommonPage.copyPageInfo(dynamicPage, dynamicVoArrayList);
    }


    /**
     * 查询动态详情
     * @param id Integer
     */
    @Override
    public DynamicVo getVoByFront(Integer id) {
        Dynamic dynamic = getById(id);
        if (ObjectUtil.isNull(dynamic)) {
            throw new MallException("动态不存在");
        }
        if(dynamic.getStatus()){
            throw new MallException("动态不存在");
        }

        DynamicVo dynamicVo = new DynamicVo();
        BeanUtils.copyProperties(dynamic, dynamicVo);
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
        return dynamicVo;
    }

    /**
     * 话题动态
     * @param request
     */
    @Override
    public PageInfo<DynamicVo>  getVoByHotFront(DynamicRequest request, PageParamRequest pageParamRequest) {
        Page<Dynamic> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Dynamic::getStatus,false);

        if(request.getPid()!=null && request.getPid()!=0){
            lambdaQueryWrapper.eq(Dynamic::getPid, request.getPid());
        }

        if(request.getIsTop()!=null){
            lambdaQueryWrapper.eq(Dynamic::getIsTop, request.getIsTop());
        }

        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.or().like(Dynamic::getTitle, request.getKeywords())
                    .or().like(Dynamic::getSynopsis, request.getKeywords())
                    .or().like(Dynamic::getContent, request.getKeywords()));
        }

        if(request.getCid() !=null && request.getCid() !=0){
            lambdaQueryWrapper.eq(Dynamic::getCid,request.getCid());
//            lambdaQueryWrapper.orderByDesc(Dynamic::getCreateTime);
        }
        if(request.getType()==1){//推荐-评论最多的
            lambdaQueryWrapper.orderByDesc(Dynamic::getReview)
                    .orderByDesc(Dynamic::getIsHot)
                    .orderByDesc(Dynamic::getSort)
                    .orderByDesc(Dynamic::getVisit);
        }else if(request.getType()==2){//精选-热门
            lambdaQueryWrapper.orderByDesc(Dynamic::getIsHot)
                    .orderByDesc(Dynamic::getSort)
                    .orderByDesc(Dynamic::getReview)
                    .orderByDesc(Dynamic::getVisit);
        }else if(request.getType()==3){ //最新的
            lambdaQueryWrapper.orderByDesc(Dynamic::getCreateTime)
                    .orderByDesc(Dynamic::getIsHot)
                    .orderByDesc(Dynamic::getSort)
                    .orderByDesc(Dynamic::getReview)
                    .orderByDesc(Dynamic::getVisit);
        }else{
            lambdaQueryWrapper.orderByDesc(Dynamic::getSort)
                    .orderByDesc(Dynamic::getCreateTime);
        }


        List<Dynamic> list = dao.selectList(lambdaQueryWrapper);

        ArrayList<DynamicVo> dynamicVoArrayList = new ArrayList<>();
        if(list ==null|| list.size()==0){
            return CommonPage.copyPageInfo(dynamicPage, dynamicVoArrayList);
        }
        for (Dynamic dynamic : list) {
            DynamicVo dynamicVo = new DynamicVo();

            BeanUtils.copyProperties(dynamic, dynamicVo);
            if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                dynamicVo.setSliderImage(dynamic.getSliderImage());
            }
            if(dynamicVo.getAdminId() ==0){
                User user = userService.getById(dynamicVo.getUid());
                if(user!=null){
                    dynamicVo.setUname(user.getNickname());
                    dynamicVo.setImg(user.getAvatar());
                    User user1 = userService.getInfo();
                    if(user1 !=null){
                        UserAttention userAttention = new UserAttention();
                        userAttention.setUid(user1.getUid());
                        userAttention.setDid(user.getUid());
                        userAttention.setType(0);
                        dynamicVo.setIsGz(isUserAttention(userAttention));

                        DynamicPraise dynamicPraise = new DynamicPraise();
                        dynamicPraise.setUid(user1.getUid());
                        dynamicPraise.setDid(dynamic.getId());
                        dynamicPraise.setType(0);
                        dynamicVo.setIsDz(isUserDynamicPraise(dynamicPraise));
                    }else{
                        dynamicVo.setIsGz(false);
                    }
                }
            }else{
                Category category = categoryService.getById(dynamicVo.getUid());
                if(category!=null){
                    dynamicVo.setUname(category.getName());
                    dynamicVo.setImg(category.getExtra());
                }
                User user1 = userService.getInfo();
                if(user1 !=null){
                    UserAttention userAttention = new UserAttention();
                    userAttention.setUid(user1.getUid());
                    userAttention.setDid(category.getId());
                    userAttention.setType(1);
                    dynamicVo.setIsGz(isUserAttention(userAttention));

                    DynamicPraise dynamicPraise = new DynamicPraise();
                    dynamicPraise.setUid(user1.getUid());
                    dynamicPraise.setDid(dynamic.getId());
                    dynamicPraise.setType(0);
                    dynamicVo.setIsDz(isUserDynamicPraise(dynamicPraise));
                }else{
                    dynamicVo.setIsGz(false);
                }
            }

            Category category = categoryService.getById(dynamicVo.getPid());
            if(category!=null){
                dynamicVo.setPname(category.getName());
            }
            if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                category = categoryService.getById(dynamicVo.getCid());
                if(category!=null){
                    dynamicVo.setCname(category.getName());
                }
            }
            dynamicVoArrayList.add(dynamicVo);
        }
        return CommonPage.copyPageInfo(dynamicPage, dynamicVoArrayList);
    }

    /**
     * 话题动态
     */
    @Override
    public PageInfo<DynamicVo>  getListDynamicByUid(Integer uid,Integer type, PageParamRequest pageParamRequest) {
        Page<Dynamic> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();

        lambdaQueryWrapper.eq(Dynamic::getUid,uid);
        lambdaQueryWrapper.eq(Dynamic::getAdminId,type);
        lambdaQueryWrapper.eq(Dynamic::getStatus,false);
        lambdaQueryWrapper.orderByDesc(Dynamic::getCreateTime);
        List<Dynamic> list = dao.selectList(lambdaQueryWrapper);

        ArrayList<DynamicVo> dynamicVoArrayList = new ArrayList<>();
        if(list ==null|| list.size()==0){
            return CommonPage.copyPageInfo(dynamicPage, dynamicVoArrayList);
        }
        for (Dynamic dynamic : list) {
            DynamicVo dynamicVo = new DynamicVo();

            BeanUtils.copyProperties(dynamic, dynamicVo);
            if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                dynamicVo.setSliderImage(dynamic.getSliderImage());
            }
            if(dynamicVo.getAdminId() ==0){
                User user = userService.getById(dynamicVo.getUid());
                if(user!=null){
                    dynamicVo.setUname(user.getNickname());
                    dynamicVo.setImg(user.getAvatar());
                    User user1 = userService.getInfo();
                    if(user1 !=null){
                        UserAttention userAttention = new UserAttention();
                        userAttention.setUid(user1.getUid());
                        userAttention.setDid(user.getUid());
                        dynamicVo.setIsGz(isUserAttention(userAttention));

                        DynamicPraise dynamicPraise = new DynamicPraise();
                        dynamicPraise.setUid(user1.getUid());
                        dynamicPraise.setDid(dynamic.getId());
                        dynamicPraise.setType(0);
                        dynamicVo.setIsDz(isUserDynamicPraise(dynamicPraise));
                    }else{
                        dynamicVo.setIsGz(false);
                    }
                }
            }else{
                Category category = categoryService.getById(dynamicVo.getUid());
                if(category!=null){
                    dynamicVo.setUname(category.getName());
                    dynamicVo.setImg(category.getExtra());
                }
                User user1 = userService.getInfo();
                if(user1 !=null){
                    UserAttention userAttention = new UserAttention();
                    userAttention.setUid(user1.getUid());
                    userAttention.setDid(category.getId());
                    dynamicVo.setIsGz(isUserAttention(userAttention));

                    DynamicPraise dynamicPraise = new DynamicPraise();
                    dynamicPraise.setUid(user1.getUid());
                    dynamicPraise.setDid(dynamic.getId());
                    dynamicPraise.setType(0);
                    dynamicVo.setIsDz(isUserDynamicPraise(dynamicPraise));
                }else{
                    dynamicVo.setIsGz(false);
                }
            }
            Category category = categoryService.getById(dynamicVo.getPid());
            if(category!=null){
                dynamicVo.setPname(category.getName());
            }
            if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                category = categoryService.getById(dynamicVo.getCid());
                if(category!=null){
                    dynamicVo.setCname(category.getName());
                }
            }
            dynamicVoArrayList.add(dynamicVo);
        }
        return CommonPage.copyPageInfo(dynamicPage, dynamicVoArrayList);
    }

    /**
     * 获取移动端热门列表
     * @return List<ArticleResponse>
     */
    @Override
    public List<DynamicVo> getHotList() {
        // 根据配置控制banner的数量
        String articleBannerLimitString = systemConfigService.getValueByKey(ARTICLE_BANNER_LIMIT);
        int articleBannerLimit = Integer.parseInt(articleBannerLimitString);
        LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Dynamic::getIsHot, true);
        lambdaQueryWrapper.eq(Dynamic::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Dynamic::getIsTop).orderByDesc(Dynamic::getIsHot).orderByDesc(Dynamic::getSort).orderByDesc(Dynamic::getVisit).orderByDesc(Dynamic::getReview).orderByDesc(Dynamic::getUpdateTime);
//        lambdaQueryWrapper.last(" limit 20");
        lambdaQueryWrapper.last(" limit " + articleBannerLimit);
        List<Dynamic> dynamicList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(dynamicList)) {
            return CollUtil.newArrayList();
        }
        List<DynamicVo> responseList = dynamicList.stream().map(e -> {
            DynamicVo dynamicVo = new DynamicVo();
            BeanUtils.copyProperties(e, dynamicVo);

            if(!StringUtils.isBlank(dynamicVo.getSliderImage()) ){
                dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamicVo.getSliderImage()));
            }

            User user = userService.getById(dynamicVo.getUid());
            if(user!=null){
                dynamicVo.setUname(user.getNickname());
            }
            Category category = categoryService.getById(dynamicVo.getPid());
            if(category!=null){
                dynamicVo.setPname(category.getName());
            }
            if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                category = categoryService.getById(dynamicVo.getCid());
                if(category!=null){
                    dynamicVo.setCname(category.getName());
                }
            }
            return dynamicVo;
        }).collect(Collectors.toList());
        return responseList;
    }

    /**
     * 获取首页话题列表
     * @return List<Category>
     */
    @Override
    public List<Category> getHomeTopicList() {
        /*QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("cid","count(review) as review");
        queryWrapper.eq("type",1);
        queryWrapper.groupBy("cid");
        queryWrapper.orderByAsc("count(review)");
        queryWrapper.last("LIMIT 3");

        List<Dynamic> list = dao.selectList(queryWrapper);
        List<Category> categoryList = new ArrayList<>();
        for(Dynamic dynamic :list){
            Category category = categoryService.getById(dynamic.getCid());
            category.setEqNum(String.valueOf(dynamic.getReview()));
            categoryList.add(category);
        }*/
        LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Category::getType, 8);
        lambdaQueryWrapper.eq(Category::getStatus, 1);
        lambdaQueryWrapper.orderByDesc(Category::getEqNum);
        lambdaQueryWrapper.last("LIMIT 3");

        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        return categoryList;
    }

    /**
     * 获取首页话题列表
     * @return List<Category>
     */
    @Override
    public  PageInfo<Category> getTopicList(PageParamRequest pageParamRequest) {
        Page<Category> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Category::getType, 8);
        lambdaQueryWrapper.eq(Category::getStatus, 1);
        lambdaQueryWrapper.orderByDesc(Category::getEqNum).orderByDesc(Category::getDevNum).orderByDesc(Category::getSort);
        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        if(categoryList ==null|| categoryList.size()==0){
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
        return CommonPage.copyPageInfo(dynamicPage, categoryList);
    }


    /**
     * 获取首页话题列表
     * @return List<Category>
     */
    @Override
    public  PageInfo<CategoryTreeVo> getMyTopicList(Integer uid, PageParamRequest pageParamRequest) {
        if(uid==null || uid ==0){
            throw new MallException("参数错误，获取失败");
        }
        Page<CategoryTreeVo> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserAttention> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(UserAttention::getUid, uid);
        lambdaQuery.eq(UserAttention::getType, 2);
        List<UserAttention>  list = userAttentionDao.selectList(lambdaQuery);
        if(list==null || list.size()==0){
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
        List<Integer> list1 = new ArrayList<>();
        for(UserAttention userAttention :list){
            list1.add(userAttention.getDid());
        }

        LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Category::getType, 8);
        lambdaQueryWrapper.eq(Category::getStatus, 1);
        lambdaQueryWrapper.in(Category::getId, list1);
        lambdaQueryWrapper.orderByDesc(Category::getEqNum).orderByDesc(Category::getDevNum).orderByDesc(Category::getSort);
        List<Category> categoryList = categoryService.list(lambdaQueryWrapper);
        if(categoryList ==null|| categoryList.size()==0){
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
        List<CategoryTreeVo> treeList = new ArrayList<>();
        for (Category category: categoryList) {
            CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
            BeanUtils.copyProperties(category, categoryTreeVo);
            categoryTreeVo.setIsGz(true);

            try {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("type", 2);
                queryWrapper.eq("did", categoryTreeVo.getId());
                categoryTreeVo.setRs(userAttentionDao.selectCount(queryWrapper));

            }catch (Exception e){
                categoryTreeVo.setRs(0);
            }
            treeList.add(categoryTreeVo);
        }
        return CommonPage.copyPageInfo(dynamicPage, treeList);
    }

    /**
     * 获取话题详情 -更新查看次数
     * @param cid
     * @return
             */
    @Override
    public CategoryTreeVo getTopic(Integer cid) {
        Category category = categoryService.getById(cid);
        if(category!=null){
            User user = userService.getInfo();
            CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
            BeanUtils.copyProperties(category, categoryTreeVo);
            if(user ==null){
                categoryTreeVo.setIsGz(false);
            }else{
                try {
                    UserAttention userAttention = new UserAttention();
                    userAttention.setUid(user.getUid());
                    userAttention.setDid(categoryTreeVo.getId());
                    userAttention.setType(2);
                    categoryTreeVo.setIsGz(dynamicService.isUserAttention(userAttention));
                }catch (Exception e){
                    categoryTreeVo.setIsGz(false);
                }
            }

            try {
                QueryWrapper queryWrapper = new QueryWrapper();
                queryWrapper.eq("type", 2);
                queryWrapper.eq("did", categoryTreeVo.getId());
                categoryTreeVo.setRs(userAttentionDao.selectCount(queryWrapper));
            }catch (Exception e){
                categoryTreeVo.setRs(0);
            }

            return  categoryTreeVo;
        }else{
            return  null;
        }
    }

    /**
     * 增加话题查看次数
     */
    @Override
    public Boolean updateTopicVisit(Integer cid) {
        try {
            Category category = categoryService.getById(cid);
            if(category!=null){
                if( category.getDevNum()==null){
                    category.setDevNum("1");
                }else{
                    category.setDevNum(String.valueOf(Integer.valueOf(category.getDevNum())+1));
                }
                return categoryService.updateById(category);
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加动态失败，errorMsg = " + e.getMessage());
            return false;
        }
    }

    /**
     * 添加动态
     * @param dynamic
     */
    @Override
    public Boolean saveDynamic(Dynamic dynamic) {
        Boolean b = false;
        try {
            if(save(dynamic)){
                if(dynamic.getType() !=null && dynamic.getType()!=0){
                    Category category = categoryService.getById(dynamic.getCid());
                    if(category!=null){
                        if( category.getEqNum()==null){
                            category.setEqNum("1");
                        }else{
                            category.setEqNum(String.valueOf(Integer.valueOf(category.getEqNum())+1));
                        }
                        categoryService.updateById(category);
                    }
                }
                b = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("添加动态失败，errorMsg = " + e.getMessage());
        }
        return b;
    }
    /**
     * 动态评论
     * @param dynamicComments
     */
    @Override
    public Boolean dynamicComments(DynamicComments dynamicComments) {
        try {
            if(dynamicCommentsDao.insert(dynamicComments)>0){
                Dynamic dynamic = getById(dynamicComments.getDid());
                if(dynamic !=null){
                    if(dynamic.getReview()==null){
                        dynamic.setReview(0);
                    }
                    dynamic.setReview(dynamic.getReview()+1);
                    dao.updateById(dynamic);
                }
                return true;
            }else{
                throw new MallException("点赞失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("评论失败，更新评论量失败，errorMsg = " + e.getMessage());
            throw new MallException(e.getMessage());
        }
    }

    /**
     * 删除评论
     * @param dynamicComments
     */
    @Override
    public Boolean deleteDynamicComments(DynamicComments dynamicComments) {
        try {
            if(dynamicComments.getId()==null || dynamicComments.getId()==0){
                throw new MallException("参数错误，删除失败!");
            }
            dynamicComments = dynamicCommentsDao.selectById(dynamicComments.getId());
            if(dynamicCommentsDao.deleteById(dynamicComments.getId())>0){

                LambdaQueryWrapper<DynamicComments> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(DynamicComments:: getPid, dynamicComments.getId());
                dynamicCommentsDao.delete(lambdaQueryWrapper);

                Dynamic dynamic = getById(dynamicComments.getDid());
                if(dynamic !=null){
                    if(dynamic.getReview()==null){
                        dynamic.setReview(0);
                    }
                    dynamic.setReview(dynamic.getReview()-1);
                    dao.updateById(dynamic);
                }
                return true;
            }else{
                throw new MallException("删除失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("删除评论失败，errorMsg = " + e.getMessage());
            throw new MallException(e.getMessage());
        }
    }

    /**
     * 动态点赞
     * @param dynamicPraise
     */
    @Override
    public Boolean dynamicPraise(DynamicPraise dynamicPraise) {
        try {
            if(dynamicPraise.getDid() ==null ||dynamicPraise.getDid() ==0
                    || dynamicPraise.getUid()==null|| dynamicPraise.getUid()==0){
                throw new MallException("参数错误!");
            }
            if(dynamicPraise.getType() ==0){ //动态点赞
                LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
                lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
                lambdaQueryWrapper.eq(DynamicPraise::getType, 0);
                DynamicPraise dynamicPraise1 = dynamicPraiseDao.selectOne(lambdaQueryWrapper);
                if(dynamicPraise1 ==null){
                    if(dynamicPraiseDao.insert(dynamicPraise)>0){
                        Dynamic dynamic = getById(dynamicPraise.getDid());
                        if(dynamic !=null){
                            if(dynamic.getVisit()==null){
                                dynamic.setVisit(0);
                            }
                            dynamic.setVisit(dynamic.getVisit()+1);
                            dao.updateById(dynamic);
                        }
                        return true;
                    }else{
                        throw new MallException("点赞失败!");
                    }
                }else{
                    throw new MallException("已经点赞!");
                }
            }else if(dynamicPraise.getType() ==1){ //分享
                if(dynamicPraiseDao.insert(dynamicPraise)>0){
                    Dynamic dynamic = getById(dynamicPraise.getDid());
                    if(dynamic !=null){
                        if(dynamic.getShare()==null){
                            dynamic.setShare(0);
                        }
                        dynamic.setShare(dynamic.getShare()+1);
                        dao.updateById(dynamic);
                    }
                    return true;
                }else{
                    throw new MallException("分享失败!");
                }
            }else if(dynamicPraise.getType() ==2){ //收藏
                LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
                lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
                lambdaQueryWrapper.eq(DynamicPraise::getType, 2);
                DynamicPraise dynamicPraise1 = dynamicPraiseDao.selectOne(lambdaQueryWrapper);
                if(dynamicPraise1 ==null){
                    if(dynamicPraiseDao.insert(dynamicPraise)>0){
                        Dynamic dynamic = getById(dynamicPraise.getDid());
                        if(dynamic !=null){
                            if(dynamic.getCollect()==null){
                                dynamic.setCollect(0);
                            }
                            dynamic.setCollect(dynamic.getCollect()+1);
                            dao.updateById(dynamic);
                        }
                        return true;
                    }else{
                        throw new MallException("收藏失败!");
                    }
                }else{
                    throw new MallException("已经收藏!");
                }
            }else if(dynamicPraise.getType() ==3){ //评论点赞
                LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
                lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
                lambdaQueryWrapper.eq(DynamicPraise::getType, 3);
                DynamicPraise dynamicPraise1 = dynamicPraiseDao.selectOne(lambdaQueryWrapper);
                if(dynamicPraise1 ==null){
                    if(dynamicPraiseDao.insert(dynamicPraise)>0){
                        return true;
                    }else{
                        throw new MallException("点赞失败!");
                    }
                }else{
                    throw new MallException("已经点赞!");
                }
            }else{
                throw new MallException("参数错误!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("操作异常，errorMsg = " + e.getMessage());
            throw new MallException(e.getMessage());
        }
    }
    /**
     * 用户取消点赞
     * @param dynamicPraise
     */
    @Override
    public Boolean deleteDynamicPraise(DynamicPraise dynamicPraise) {
        try {
            if(dynamicPraise.getUid()==null || dynamicPraise.getDid()==null){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
            lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
            lambdaQueryWrapper.eq(DynamicPraise::getType, 0);
            if(dynamicPraiseDao.delete(lambdaQueryWrapper)>0){
                Dynamic dynamic = getById(dynamicPraise.getDid());
                if(dynamic !=null){
                    if(dynamicPraise.getType() ==0){ //点赞
                        if(dynamic.getVisit()==null){
                            dynamic.setVisit(0);
                        }
                        if(dynamic.getVisit()>0){
                            dynamic.setVisit(dynamic.getVisit()-1);
                            dao.updateById(dynamic);
                        }
                    }
                }

                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            throw new MallException(e.getMessage());
        }
    }
    /**
     * 用户取消收藏
     * @param dynamicPraise
     */
    @Override
    public Boolean deleteDynamicCollet(DynamicPraise dynamicPraise) {
        try {
            if(dynamicPraise.getUid()==null || dynamicPraise.getDid()==null){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
            lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
            lambdaQueryWrapper.eq(DynamicPraise::getType, 2);
            if(dynamicPraiseDao.delete(lambdaQueryWrapper)>0){
                Dynamic dynamic = getById(dynamicPraise.getDid());
                if(dynamic !=null){
                    if(dynamicPraise.getType() ==2){ //收藏
                        if(dynamic.getCollect()==null){
                            dynamic.setCollect(0);
                        }
                        if(dynamic.getVisit()>0){
                            dynamic.setCollect(dynamic.getCollect()-1);
                            dao.updateById(dynamic);
                        }
                    }
                }
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            throw new MallException(e.getMessage());
        }
    }
    /**
     * 用户取消评论点赞
     * @param dynamicPraise
     */
    @Override
    public Boolean deleteCommentsPraise(DynamicPraise dynamicPraise) {
        try {
            if(dynamicPraise.getUid()==null || dynamicPraise.getDid()==null){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
            lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
            lambdaQueryWrapper.eq(DynamicPraise::getType, 3);
            if(dynamicPraiseDao.delete(lambdaQueryWrapper)>0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            throw new MallException(e.getMessage());
        }
    }
    /**
     * 用户关注
     * @param userAttention
     */
    @Override
    public Boolean userAttention(UserAttention userAttention) {
        try {
            if(userAttention.getUid()==null || userAttention.getDid()==null){
                throw new MallException("参数错误!");
            }
            LambdaQueryWrapper<UserAttention> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(UserAttention::getUid, userAttention.getUid());
            lambdaQueryWrapper.eq(UserAttention::getDid, userAttention.getDid());
            UserAttention userAttention1 = userAttentionDao.selectOne(lambdaQueryWrapper);
            if(userAttention1 == null){
                if(userAttention.getName()==null || userAttention.getName().equals("") ||
                        userAttention.getImg()==null || userAttention.getImg().equals("")){
                    User user = userService.getById(userAttention.getDid());
                    userAttention.setName(user.getNickname());
                    userAttention.setImg(user.getAvatar());
                }
                if(userAttentionDao.insert(userAttention)>0){
                    return true;
                }else{
                    return false;
                }
            }else{
                throw new MallException("该用户已关注!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("关注失败，errorMsg = " + e.getMessage());
            throw new MallException(e.getMessage());
        }
    }

    /**
     * 用户是否关注
     * @param dynamicPraise
     */
    @Override
    public Boolean isUserDynamicPraise(DynamicPraise dynamicPraise) {
        try {
            if(dynamicPraise.getUid()==null || dynamicPraise.getDid()==null){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
            lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
            lambdaQueryWrapper.eq(DynamicPraise::getType, dynamicPraise.getType());
            DynamicPraise dynamicPraise1 = dynamicPraiseDao.selectOne(lambdaQueryWrapper);
            if(dynamicPraise1 == null){
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            throw new MallException(e.getMessage());
        }
    }
    /**
     * 用户是否收藏
     * @param dynamicPraise
     */
    @Override
    public Boolean isUserDynamicCollect(DynamicPraise dynamicPraise) {
        try {
            if(dynamicPraise.getUid()==null || dynamicPraise.getDid()==null){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
            lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
            lambdaQueryWrapper.eq(DynamicPraise::getType, 2);
            DynamicPraise dynamicPraise1 = dynamicPraiseDao.selectOne(lambdaQueryWrapper);
            if(dynamicPraise1 == null){
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            throw new MallException(e.getMessage());
        }
    }

    /**
     * 用户是否关注
     * @param userAttention
     */
    @Override
    public Boolean isUserAttention(UserAttention userAttention) {
        try {
            if(userAttention.getUid()==null || userAttention.getDid()==null){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<UserAttention> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(UserAttention::getUid, userAttention.getUid());
            lambdaQueryWrapper.eq(UserAttention::getDid, userAttention.getDid());
            if(userAttention.getType()==null){
                userAttention.setType(0);
            }
            lambdaQueryWrapper.eq(UserAttention::getType, userAttention.getType());
            UserAttention userAttention1 = userAttentionDao.selectOne(lambdaQueryWrapper);
            if(userAttention1 == null){
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            throw new MallException(e.getMessage());
        }
    }
    /**
     * 用户取消关注
     * @param userAttention
     */
    @Override
    public Boolean deleteUserAttention(UserAttention userAttention) {
        try {
            if(userAttention.getUid()==null || userAttention.getDid()==null){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<UserAttention> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(UserAttention::getUid, userAttention.getUid());
            lambdaQueryWrapper.eq(UserAttention::getDid, userAttention.getDid());
            lambdaQueryWrapper.eq(UserAttention::getType, userAttention.getType());
            if(userAttentionDao.delete(lambdaQueryWrapper)>0){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            throw new MallException(e.getMessage());
        }
    }

    @Override
    public PageInfo<DynamicCommentsVo> getCommentsList(DynamicComments request, PageParamRequest pageParamRequest) {
        Page<DynamicCommentsVo> dynamicCommentsVoPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<DynamicComments> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(DynamicComments::getDid,request.getDid());
        if(request.getName() !=null && !request.getName().equals("")){
            lambdaQueryWrapper.like(DynamicComments::getName, request.getName());
        }
        if(request.getPid()!=null && request.getPid()>0){
            lambdaQueryWrapper.eq(DynamicComments::getPid, request.getPid());
        }else{
            lambdaQueryWrapper.eq(DynamicComments::getPid, 0);
        }
        lambdaQueryWrapper.orderByDesc(DynamicComments::getCreateTime);
        List<DynamicComments> dynamicList = dynamicCommentsDao.selectList(lambdaQueryWrapper);

        ArrayList<DynamicCommentsVo> dynamicCommentsVoArrayList = new ArrayList<>();
        if(dynamicList ==null|| dynamicList.size()==0){
            return CommonPage.copyPageInfo(dynamicCommentsVoPage, dynamicCommentsVoArrayList);
        }

        for (DynamicComments dynamicComments : dynamicList){
            DynamicCommentsVo dynamicCommentsVo = new DynamicCommentsVo();
            BeanUtils.copyProperties(dynamicComments, dynamicCommentsVo);
            User user1 = userService.getInfo();
            if(user1 !=null) {
                DynamicPraise dynamicPraise = new DynamicPraise();
                dynamicPraise.setUid(user1.getUid());
                dynamicPraise.setDid(dynamicComments.getId());
                dynamicPraise.setType(3);
                dynamicCommentsVo.setIsDz(isUserDynamicPraise(dynamicPraise));

                LambdaQueryWrapper<DynamicPraise> lambdaQuery = Wrappers.lambdaQuery();
                lambdaQuery.eq(DynamicPraise::getDid,dynamicComments.getId());
                lambdaQuery.eq(DynamicPraise::getType,3);
                dynamicCommentsVo.setDzcs(dynamicPraiseDao.selectCount(lambdaQuery));

                LambdaQueryWrapper<DynamicComments> lambdaQuery1 = Wrappers.lambdaQuery();
                lambdaQuery1.eq(DynamicComments::getPid,dynamicComments.getId());
                dynamicCommentsVo.setPlcs(dynamicCommentsDao.selectCount(lambdaQuery1));
            }

            LambdaQueryWrapper<DynamicComments> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(DynamicComments::getDid,dynamicComments.getDid());
//            lambdaQuery.gt(DynamicComments::getPid,0);
            lambdaQuery.eq(DynamicComments::getPid,dynamicComments.getId());
            if(request.getName() !=null && !request.getName().equals("")){
                lambdaQuery.like(DynamicComments::getName, request.getName());
            }
            lambdaQuery.orderByDesc(DynamicComments::getCreateTime);
//            lambdaQuery.last("limit 3");
            dynamicCommentsVo.setChild(dynamicCommentsDao.selectList(lambdaQuery));
            dynamicCommentsVoArrayList.add(dynamicCommentsVo);
        }
        return CommonPage.copyPageInfo(dynamicCommentsVoPage, dynamicCommentsVoArrayList);
    }



    @Override
    public PageInfo<DynamicPraise> getPraiseList(DynamicPraise request, PageParamRequest pageParamRequest) {
        Page<DynamicPraise> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(DynamicPraise::getDid,request.getDid());
        lambdaQueryWrapper.eq(DynamicPraise::getType,request.getType());
        if(request.getName() !=null && !request.getName().equals("")){
            lambdaQueryWrapper.like(DynamicPraise::getName, request.getName());
        }
        lambdaQueryWrapper.orderByDesc(DynamicPraise::getCreateTime);
        List<DynamicPraise> dynamicList = dynamicPraiseDao.selectList(lambdaQueryWrapper);
        return CommonPage.copyPageInfo(dynamicPage, dynamicList);
    }


    /**
     * 评论详情
     * @return
     */
    @Override
    public DynamicCommentsVo getCommentsInfo(Integer id) {
        DynamicComments dynamicComments = dynamicCommentsDao.selectById(id);
        if(dynamicComments !=null)
        {
            DynamicCommentsVo dynamicCommentsVo = new DynamicCommentsVo();
            BeanUtils.copyProperties(dynamicComments, dynamicCommentsVo);
            User user1 = userService.getInfo();
            if(user1 !=null) {
                DynamicPraise dynamicPraise = new DynamicPraise();
                dynamicPraise.setUid(user1.getUid());
                dynamicPraise.setDid(dynamicComments.getId());
                dynamicPraise.setType(3);
                dynamicCommentsVo.setIsDz(isUserDynamicPraise(dynamicPraise));

                LambdaQueryWrapper<DynamicPraise> lambdaQuery = Wrappers.lambdaQuery();
                lambdaQuery.eq(DynamicPraise::getDid,dynamicComments.getId());
                lambdaQuery.eq(DynamicPraise::getType,3);
                dynamicCommentsVo.setDzcs(dynamicPraiseDao.selectCount(lambdaQuery));

                LambdaQueryWrapper<DynamicComments> lambdaQuery1 = Wrappers.lambdaQuery();
                lambdaQuery1.eq(DynamicComments::getPid,dynamicComments.getId());
                dynamicCommentsVo.setPlcs(dynamicCommentsDao.selectCount(lambdaQuery1));
            }
            return dynamicCommentsVo;
        }else{
            return null;
        }
    }


    @Override
    public PageInfo<DynamicCommentsVo> getCommentsChildList(DynamicComments request, PageParamRequest pageParamRequest) {
        Page<DynamicCommentsVo> dynamicCommentsVoPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<DynamicComments> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(request.getName() !=null && !request.getName().equals("")){
            lambdaQueryWrapper.like(DynamicComments::getName, request.getName());
        }
        if(request.getPid()!=null && request.getPid()>0){
            lambdaQueryWrapper.eq(DynamicComments::getPid, request.getPid());
        }else{
            lambdaQueryWrapper.eq(DynamicComments::getPid, request.getId());
        }
        lambdaQueryWrapper.orderByDesc(DynamicComments::getCreateTime);
        List<DynamicComments> dynamicList = dynamicCommentsDao.selectList(lambdaQueryWrapper);

        ArrayList<DynamicCommentsVo> dynamicCommentsVoArrayList = new ArrayList<>();
        if(dynamicList ==null|| dynamicList.size()==0){
            return CommonPage.copyPageInfo(dynamicCommentsVoPage, dynamicCommentsVoArrayList);
        }

        for (DynamicComments dynamicComments : dynamicList){
            DynamicCommentsVo dynamicCommentsVo = new DynamicCommentsVo();
            BeanUtils.copyProperties(dynamicComments, dynamicCommentsVo);
            User user1 = userService.getInfo();
            if(user1 !=null) {
                DynamicPraise dynamicPraise = new DynamicPraise();
                dynamicPraise.setUid(user1.getUid());
                dynamicPraise.setDid(dynamicComments.getId());
                dynamicPraise.setType(3);
                dynamicCommentsVo.setIsDz(isUserDynamicPraise(dynamicPraise));

                LambdaQueryWrapper<DynamicPraise> lambdaQuery = Wrappers.lambdaQuery();
                lambdaQuery.eq(DynamicPraise::getDid,dynamicComments.getId());
                lambdaQuery.eq(DynamicPraise::getType,3);
                dynamicCommentsVo.setDzcs(dynamicPraiseDao.selectCount(lambdaQuery));

                LambdaQueryWrapper<DynamicComments> lambdaQuery1 = Wrappers.lambdaQuery();
                lambdaQuery1.eq(DynamicComments::getPid,dynamicComments.getId());
                dynamicCommentsVo.setPlcs(dynamicCommentsDao.selectCount(lambdaQuery1));
            }
            dynamicCommentsVoArrayList.add(dynamicCommentsVo);
        }
        return CommonPage.copyPageInfo(dynamicCommentsVoPage, dynamicCommentsVoArrayList);
    }


    @Override
    public PageInfo<UserAttentionVo> getListGzUser(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserAttention> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        User user = null;
        if(uid==null || uid==0){
            user = userService.getInfo();
        }else{
            user = userService.getById(uid);
        }
        if(user !=null){
            List<Integer> list1 = new ArrayList<>();
            list1.add(0);
            list1.add(1);
            LambdaQueryWrapper<UserAttention> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(UserAttention::getUid,user.getUid());
            lambdaQueryWrapper.in(UserAttention::getType,list1);
            lambdaQueryWrapper.orderByDesc(UserAttention::getCreateTime);
            List<UserAttention> list = userAttentionDao.selectList(lambdaQueryWrapper);
            if(list!=null && list.size()>0){
                User user2 = userService.getInfo();
                List<UserAttentionVo> userAttentionVoList = new ArrayList<>();
                for (UserAttention userAttention : list){
                    UserAttentionVo userAttentionVo = new UserAttentionVo();
                    BeanUtils.copyProperties(userAttention, userAttentionVo);

                    LambdaQueryWrapper<UserAttention> lambdaQuery = Wrappers.lambdaQuery();
                    lambdaQuery.eq(UserAttention::getUid,user2.getUid());
                    lambdaQuery.eq(UserAttention::getDid,userAttention.getDid());
                    if(userAttentionDao.selectOne(lambdaQuery) ==null){
                        userAttentionVo.setIsGz(false);
                    }else{
                        userAttentionVo.setIsGz(true);
                    }
                    userAttentionVoList.add(userAttentionVo);
                }
                return CommonPage.copyPageInfo(dynamicPage, userAttentionVoList);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
//            return CommonPage.copyPageInfo(dynamicPage, userAttentionDao.selectList(lambdaQueryWrapper));
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }

    @Override
    public PageInfo<UserAttentionVo> getListFsUser(Integer uid, PageParamRequest pageParamRequest) {
        Page<UserAttention> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<UserAttention> lambdaQueryWrapper = Wrappers.lambdaQuery();
        User user = null;
        if(uid==null || uid==0){
            user = userService.getInfo();
        }else{
            user = userService.getById(uid);
        }
        if(user !=null){
            User user2 = userService.getInfo();
            lambdaQueryWrapper.eq(UserAttention::getDid,user.getUid());
            lambdaQueryWrapper.orderByDesc(UserAttention::getCreateTime);
            List<UserAttention> list = userAttentionDao.selectList(lambdaQueryWrapper);
            if(list!=null && list.size()>0){
                List<UserAttentionVo> userAttentionVoList = new ArrayList<>();
                for (UserAttention userAttention : list){
                    User user1 = userService.getById(userAttention.getUid());
                    if(user1 ==null ){
                        continue;
                    }
                    userAttention.setName(user1.getNickname());
                    userAttention.setImg(user1.getAvatar());
                    UserAttentionVo userAttentionVo = new UserAttentionVo();
                    BeanUtils.copyProperties(userAttention, userAttentionVo);

                    LambdaQueryWrapper<UserAttention> lambdaQuery = Wrappers.lambdaQuery();

                    lambdaQuery.eq(UserAttention::getUid,user2.getUid());
                    lambdaQuery.eq(UserAttention::getDid,userAttention.getUid());
                    if(userAttentionDao.selectOne(lambdaQuery) ==null){
                        userAttentionVo.setIsGz(false);
                    }else{
                        userAttentionVo.setIsGz(true);
                    }
                    userAttentionVoList.add(userAttentionVo);
                }
                return CommonPage.copyPageInfo(dynamicPage, userAttentionVoList);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }


    /**
     * 用户消息
     */
    @Override
    public MessageInfoResponse getMessageInfo() {
        //通知公告
        MessageInfoResponse messageInfoResponse = new MessageInfoResponse();
        List<HashMap<String, Object>> rollList = systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_INDEX_NEWS_BANNER);
        if(rollList!=null && rollList.size()>0){
            messageInfoResponse.setRoll(rollList.get(0)); //首页滚动新闻
        }
        User user = userService.getInfo();

        if(user != null){
            LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(Dynamic::getUid,user.getUid());
            lambdaQueryWrapper.eq(Dynamic::getStatus,false);
            List<Dynamic> dynamicList = dao.selectList(lambdaQueryWrapper);
            List<Integer> listDynamicId = new ArrayList<>();
            if(dynamicList!=null && dynamicList.size()>0){
                for(Dynamic dynamic : dynamicList){
                    listDynamicId.add(dynamic.getId());
                }
            }else{
                listDynamicId.add(0);
            }
            //最新用户评论动态
            LambdaQueryWrapper<DynamicComments> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.in(DynamicComments::getDid,listDynamicId);
            lambdaQuery.eq(DynamicComments::getFlag,false);
            lambdaQuery.orderByDesc(DynamicComments::getCreateTime);
//            lambdaQuery.last("limit 1");
            List<DynamicComments> dynamicCommentsList = dynamicCommentsDao.selectList(lambdaQuery);
            messageInfoResponse.setDynamicComments(dynamicCommentsList);
//            if(dynamicCommentsList!=null && dynamicCommentsList.size()>0){
//                messageInfoResponse.setDynamicComments(dynamicCommentsList.get(0));
//            }

            //最新用户动态点赞
            LambdaQueryWrapper<DynamicPraise> lambdaQuery1 = Wrappers.lambdaQuery();
            lambdaQuery1.in(DynamicPraise::getDid,listDynamicId);
            lambdaQuery1.eq(DynamicPraise::getType,0); //动态点赞
            lambdaQuery1.eq(DynamicPraise::getFlag,false);
            lambdaQuery1.orderByDesc(DynamicPraise::getCreateTime);
//            lambdaQuery1.last("limit 1");
            List<DynamicPraise> dynamicPraiseList = dynamicPraiseDao.selectList(lambdaQuery1);
            messageInfoResponse.setDtDz(dynamicPraiseList);
//            if(dynamicPraiseList!=null && dynamicPraiseList.size()>0){
//                messageInfoResponse.setDtDz(dynamicPraiseList.get(0));
//            }

            //最新用户评论点赞
            LambdaQueryWrapper<DynamicPraise> lambdaQuery2 = Wrappers.lambdaQuery();
            lambdaQuery2.in(DynamicPraise::getDid,listDynamicId);
            lambdaQuery2.eq(DynamicPraise::getType,3); //评论点赞
            lambdaQuery2.eq(DynamicPraise::getFlag,false);
            lambdaQuery2.orderByDesc(DynamicPraise::getCreateTime);
//            lambdaQuery2.last("limit 1");
            List<DynamicPraise> dynamicPraiseList1 = dynamicPraiseDao.selectList(lambdaQuery2);
            messageInfoResponse.setPlDz(dynamicPraiseList1);
            //            if(dynamicPraiseList1!=null && dynamicPraiseList1.size()>0){
//                messageInfoResponse.setPlDz(dynamicPraiseList1.get(0));
//            }

            //最新用户动态置顶
            LambdaQueryWrapper<Dynamic> lambdaQueryWrapper2 = Wrappers.lambdaQuery();
            lambdaQueryWrapper2.eq(Dynamic::getUid,user.getUid());
            lambdaQueryWrapper2.eq(Dynamic::getIsTop,true);
            lambdaQueryWrapper2.eq(Dynamic::getStatus,false);
            lambdaQueryWrapper2.orderByDesc(Dynamic::getUpdateTime);
            lambdaQueryWrapper2.last("limit 1");
            List<Dynamic> dynamicList1 = dao.selectList(lambdaQueryWrapper2);
            if(dynamicList1!=null && dynamicList1.size()>0){
                messageInfoResponse.setDynamicTop(dynamicList1.get(0));
            }else{
                messageInfoResponse.setDynamicTop(new Dynamic());
            }
            //最新报名活动用户
            LambdaQueryWrapper<Activity> lambdaQuery3 = Wrappers.lambdaQuery();
            lambdaQuery3.eq(Activity::getUid,user.getUid());
            lambdaQuery3.eq(Activity::getStatus,false);
            List<Activity> activityList = activityService.list(lambdaQuery3);
            List<Integer> listActivityId = new ArrayList<>();
            if(activityList!=null && activityList.size()>0){
                for(Activity activity : activityList){
                    listActivityId.add(activity.getId());
                }
            }else{
                listActivityId.add(0);
            }
            LambdaQueryWrapper<ActivitySign> lambdaQuery4 = Wrappers.lambdaQuery();
            lambdaQuery4.in(ActivitySign::getDid,listActivityId);
            lambdaQuery4.eq(ActivitySign::getFlag,false);
            lambdaQuery4.orderByDesc(ActivitySign::getCreateTime);
//            lambdaQuery4.last("limit 1");
            List<ActivitySign> activitySignList = activitySignDao.selectList(lambdaQuery4);
            messageInfoResponse.setActivitySign(activitySignList);
//            if(activitySignList!=null && activitySignList.size()>0){
//                messageInfoResponse.setActivitySign(activitySignList.get(0));
//            }
        }
        return messageInfoResponse;
    }


    @Override
    public PageInfo<DynamicCommentsVo> getMsgListDynamicComments(PageParamRequest pageParamRequest) {
        Page<DynamicComments> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        User user = userService.getInfo();
        if(user != null) {
            LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(Dynamic::getUid, user.getUid());
            lambdaQueryWrapper.eq(Dynamic::getStatus, false);
            List<Dynamic> dynamicList = dao.selectList(lambdaQueryWrapper);
            List<Integer> listDynamicId = new ArrayList<>();
            //最新用户评论动态
            LambdaQueryWrapper<DynamicComments> lambdaQuery = Wrappers.lambdaQuery();
            if (dynamicList != null && dynamicList.size() > 0) {
                for (Dynamic dynamic : dynamicList) {
                    listDynamicId.add(dynamic.getId());
                }
                lambdaQuery.in(DynamicComments::getDid, listDynamicId);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
            lambdaQuery.orderByDesc(DynamicComments::getCreateTime);
            List<DynamicComments> dynamicCommentsList = dynamicCommentsDao.selectList(lambdaQuery);
            if(dynamicCommentsList!=null && dynamicCommentsList.size()>0){
                List<DynamicCommentsVo> dynamicCommentsVoList =  new ArrayList<>();
                for (DynamicComments dynamicComments : dynamicCommentsList){
                    if(!dynamicComments.getFlag()){
                        dynamicComments.setFlag(true);
                        dynamicCommentsDao.updateById(dynamicComments);
                    }
                    DynamicCommentsVo dynamicCommentsVo = new DynamicCommentsVo();
                    BeanUtils.copyProperties(dynamicComments, dynamicCommentsVo);
                    Dynamic dynamic = getById(dynamicComments.getDid());
                    if(dynamic!=null){
                        DynamicVo dynamicVo = new DynamicVo();
                        BeanUtils.copyProperties(dynamic, dynamicVo);

                        if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                            dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                            dynamicVo.setSliderImage(dynamic.getSliderImage());
                        }
                        Category category = categoryService.getById(dynamicVo.getPid());
                        if(category!=null){
                            dynamicVo.setPname(category.getName());
                        }
                        if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                            category = categoryService.getById(dynamicVo.getCid());
                            if(category!=null){
                                dynamicVo.setCname(category.getName());
                            }
                        }
                        dynamicCommentsVo.setDynamic(dynamicVo);
                    }
                    dynamicCommentsVoList.add(dynamicCommentsVo);
                }
                return CommonPage.copyPageInfo(dynamicPage, dynamicCommentsVoList);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }


    @Override
    public PageInfo<DynamicPraiseVo> getMsgListDtDz(PageParamRequest pageParamRequest) {
        Page<DynamicPraise> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        User user = userService.getInfo();
        if(user != null) {
            LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(Dynamic::getUid, user.getUid());
            lambdaQueryWrapper.eq(Dynamic::getStatus, false);
            List<Dynamic> dynamicList = dao.selectList(lambdaQueryWrapper);
            List<Integer> listDynamicId = new ArrayList<>();
            //最新用户动态点赞
            LambdaQueryWrapper<DynamicPraise> lambdaQuery1 = Wrappers.lambdaQuery();
            if (dynamicList != null && dynamicList.size() > 0) {
                for (Dynamic dynamic : dynamicList) {
                    listDynamicId.add(dynamic.getId());
                }
                lambdaQuery1.in(DynamicPraise::getDid,listDynamicId);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
            lambdaQuery1.eq(DynamicPraise::getType,0); //动态点赞
            lambdaQuery1.orderByDesc(DynamicPraise::getCreateTime);
            List<DynamicPraise> dynamicPraiseList = dynamicPraiseDao.selectList(lambdaQuery1);
            if(dynamicPraiseList!=null && dynamicPraiseList.size()>0){
                List<DynamicPraiseVo> dynamicPraiseVoList =  new ArrayList<>();
                for (DynamicPraise dynamicPraise : dynamicPraiseList){
                    if(!dynamicPraise.getFlag()){
                        dynamicPraise.setFlag(true);
                        dynamicPraiseDao.updateById(dynamicPraise);
                    }

                    DynamicPraiseVo dynamicPraiseVo = new DynamicPraiseVo();
                    BeanUtils.copyProperties(dynamicPraise, dynamicPraiseVo);
                    Dynamic dynamic = getById(dynamicPraise.getDid());

                    if(dynamic!=null){
                        DynamicVo dynamicVo = new DynamicVo();
                        BeanUtils.copyProperties(dynamic, dynamicVo);

                        if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                            dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                            dynamicVo.setSliderImage(dynamic.getSliderImage());
                        }
                        Category category = categoryService.getById(dynamicVo.getPid());
                        if(category!=null){
                            dynamicVo.setPname(category.getName());
                        }
                        if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                            category = categoryService.getById(dynamicVo.getCid());
                            if(category!=null){
                                dynamicVo.setCname(category.getName());
                            }
                        }
                        dynamicPraiseVo.setDynamic(dynamicVo);
                    }

                    dynamicPraiseVoList.add(dynamicPraiseVo);
                }
                return CommonPage.copyPageInfo(dynamicPage, dynamicPraiseVoList);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }
    @Override
    public PageInfo<DynamicPraiseVo> getMsgListPlDz(PageParamRequest pageParamRequest) {
        Page<DynamicPraise> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        User user = userService.getInfo();
        if(user != null) {
            LambdaQueryWrapper<Dynamic> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(Dynamic::getUid, user.getUid());
            lambdaQueryWrapper.eq(Dynamic::getStatus, false);
            List<Dynamic> dynamicList = dao.selectList(lambdaQueryWrapper);
            List<Integer> listDynamicId = new ArrayList<>();
            //最新用户动态点赞
            LambdaQueryWrapper<DynamicPraise> lambdaQuery1 = Wrappers.lambdaQuery();
            if (dynamicList != null && dynamicList.size() > 0) {
                for (Dynamic dynamic : dynamicList) {
                    listDynamicId.add(dynamic.getId());
                }
                lambdaQuery1.in(DynamicPraise::getDid,listDynamicId);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
            lambdaQuery1.eq(DynamicPraise::getType,3); //评论点赞
            lambdaQuery1.orderByDesc(DynamicPraise::getCreateTime);
            List<DynamicPraise> dynamicPraiseList = dynamicPraiseDao.selectList(lambdaQuery1);
            if(dynamicPraiseList!=null && dynamicPraiseList.size()>0){
                List<DynamicPraiseVo> dynamicPraiseVoList =  new ArrayList<>();
                for (DynamicPraise dynamicPraise : dynamicPraiseList){
                    if(!dynamicPraise.getFlag()){
                        dynamicPraise.setFlag(true);
                        dynamicPraiseDao.updateById(dynamicPraise);
                    }
                    DynamicPraiseVo dynamicPraiseVo = new DynamicPraiseVo();
                    BeanUtils.copyProperties(dynamicPraise, dynamicPraiseVo);
                    Dynamic dynamic = getById(dynamicPraise.getDid());

                    if(dynamic!=null){
                        DynamicVo dynamicVo = new DynamicVo();
                        BeanUtils.copyProperties(dynamic, dynamicVo);

                        if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                            dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                            dynamicVo.setSliderImage(dynamic.getSliderImage());
                        }
                        Category category = categoryService.getById(dynamicVo.getPid());
                        if(category!=null){
                            dynamicVo.setPname(category.getName());
                        }
                        if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                            category = categoryService.getById(dynamicVo.getCid());
                            if(category!=null){
                                dynamicVo.setCname(category.getName());
                            }
                        }
                        dynamicPraiseVo.setDynamic(dynamicVo);
                    }

                    dynamicPraiseVoList.add(dynamicPraiseVo);
                }
                return CommonPage.copyPageInfo(dynamicPage, dynamicPraiseVoList);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }

    @Override
    public PageInfo<DynamicVo> getMsgLListDynamicTop(PageParamRequest pageParamRequest) {
        Page<Dynamic> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        User user = userService.getInfo();
        if(user != null) {
            //最新用户动态置顶
            LambdaQueryWrapper<Dynamic> lambdaQueryWrapper2 = Wrappers.lambdaQuery();
            lambdaQueryWrapper2.eq(Dynamic::getUid,user.getUid());
            lambdaQueryWrapper2.eq(Dynamic::getIsTop,true);
            lambdaQueryWrapper2.eq(Dynamic::getStatus,false);
            lambdaQueryWrapper2.orderByDesc(Dynamic::getUpdateTime);
            List<Dynamic> dynamicList1 = dao.selectList(lambdaQueryWrapper2);
            List<DynamicVo> dynamicVoArrayList = new ArrayList<>();
            for (Dynamic dynamic:dynamicList1){
                DynamicVo dynamicVo = new DynamicVo();
                BeanUtils.copyProperties(dynamic, dynamicVo);

                User user1 = userService.getById(dynamic.getUid());
                if(user1 !=null){
                    dynamicVo.setUname(user1.getNickname());
                    dynamicVo.setImg(user1.getAvatar());
                }
                if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                    dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                    dynamicVo.setSliderImage(dynamic.getSliderImage());
                }
                Category category = categoryService.getById(dynamicVo.getPid());
                if(category!=null){
                    dynamicVo.setPname(category.getName());
                }
                if(dynamicVo.getType()==1 && dynamicVo.getCid()!=null && dynamicVo.getCid()>0){
                    category = categoryService.getById(dynamicVo.getCid());
                    if(category!=null){
                        dynamicVo.setCname(category.getName());
                    }
                }
                dynamicVoArrayList.add(dynamicVo);
            }
            return CommonPage.copyPageInfo(dynamicPage, dynamicVoArrayList);
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }

    @Override
    public PageInfo<ActivitySignVo> getMsgListActivitySign(PageParamRequest pageParamRequest) {
        Page<ActivitySign> dynamicPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        User user = userService.getInfo();
        if(user != null) {
            //最新报名活动用户
            LambdaQueryWrapper<Activity> lambdaQuery3 = Wrappers.lambdaQuery();
            lambdaQuery3.eq(Activity::getUid,user.getUid());
            lambdaQuery3.eq(Activity::getStatus,false);
            List<Activity> activityList = activityService.list(lambdaQuery3);
            List<Integer> listActivityId = new ArrayList<>();
            LambdaQueryWrapper<ActivitySign> lambdaQuery4 = Wrappers.lambdaQuery();
            if(activityList!=null && activityList.size()>0){
                for(Activity activity : activityList){
                    listActivityId.add(activity.getId());
                }
                lambdaQuery4.in(ActivitySign::getDid,listActivityId);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
            lambdaQuery4.orderByDesc(ActivitySign::getCreateTime);
            List<ActivitySign> activitySignList = activitySignDao.selectList(lambdaQuery4);

            if(activitySignList!=null && activitySignList.size()>0){
                List<ActivitySignVo> activitySignVoList =  new ArrayList<>();
                for (ActivitySign activitySign : activitySignList){
                    if(!activitySign.getFlag()){
                        activitySign.setFlag(true);
                        activitySignDao.updateById(activitySign);
                    }

                    ActivitySignVo activitySignVo = new ActivitySignVo();
                    BeanUtils.copyProperties(activitySign, activitySignVo);
                    Activity activity =activityService.getById(activitySign.getDid());

                    if(activity!=null){
                        ActivityVo activityVo = new ActivityVo();
                        BeanUtils.copyProperties(activity, activityVo);

                        if(!StringUtils.isBlank(activity.getSliderImage()) ){
                            activityVo.setSliderImages(MallUtil.jsonToListString(activity.getSliderImage()));
                        }
                        Category category = categoryService.getById(activity.getPid());
                        if(category!=null){
                            activityVo.setPname(category.getName());
                        }
                        category = categoryService.getById(activity.getCid());
                        if(category!=null){
                            activityVo.setCname(category.getName());
                        }
                        activitySignVo.setActivity(activityVo);
                    }

                    activitySignVoList.add(activitySignVo);
                }
                return CommonPage.copyPageInfo(dynamicPage, activitySignVoList);
            }else{
                return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
            }
        }else{
            return CommonPage.copyPageInfo(dynamicPage, new ArrayList<>());
        }
    }
}

