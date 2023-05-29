package com.hdkj.mall.front.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.MallException;
import com.hdkj.mall.article.dao.ActivityCollectDao;
import com.hdkj.mall.article.dao.ActivitySignDao;
import com.hdkj.mall.article.dao.DynamicPraiseDao;
import com.hdkj.mall.article.dao.UserAttentionDao;
import com.hdkj.mall.article.model.*;
import com.hdkj.mall.article.request.ActivityRequest;
import com.hdkj.mall.article.service.ActivityService;
import com.hdkj.mall.article.service.DynamicService;
import com.hdkj.mall.article.vo.*;
import com.hdkj.mall.category.request.CategorySearchRequest;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.front.response.MessageInfoResponse;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.system.service.SystemGroupDataService;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.utils.MallUtil;
import com.hdkj.mall.article.request.DynamicRequest;
import com.hdkj.mall.article.service.ArticleService;
import com.hdkj.mall.front.response.ArticleResponse;
import com.hdkj.mall.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 发现
 */
@Slf4j
@RestController("FoundFrontController")
@RequestMapping("api/front/found")
@Api(tags = "发现")

public class FoundController {

    @Autowired
    private DynamicService dynamicService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private ArticleService articleService;
    @Resource
    private ActivitySignDao activitySignDao;
    @Resource
    private ActivityCollectDao activityCollectDao;
    @Resource
    private DynamicPraiseDao dynamicPraiseDao;
    @Resource
    private UserAttentionDao userAttentionDao;

    /**
     * 获取首页话题列表
     */
    @ApiOperation(value = "首页话题列表")
    @RequestMapping(value = "/topic/home", method = RequestMethod.GET)
    public CommonResult<List<Category>> getHomeTopicList(){
        return CommonResult.success(dynamicService.getHomeTopicList());
    }
    /**
     * 获取首页话题列表
     */
    @ApiOperation(value = "话题列表")
    @RequestMapping(value = "/topic/listTopic", method = RequestMethod.GET)
    public CommonResult<CommonPage<Category>> getTopicList(Integer page, Integer limit){
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(page);
        pageParamRequest.setLimit(limit);
        return CommonResult.success(CommonPage.restPage(dynamicService.getTopicList(pageParamRequest)));
    }
    /**
     * 获取首页话题列表
     */
    @ApiOperation(value = "我的话题列表")
    @RequestMapping(value = "/topic/myListTopic", method = RequestMethod.GET)
    public CommonResult<CommonPage<CategoryTreeVo>> getMyTopicList(Integer uid, Integer page, Integer limit){
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(page);
        pageParamRequest.setLimit(limit);
        return CommonResult.success(CommonPage.restPage(dynamicService.getMyTopicList(uid,pageParamRequest)));
    }
    /**
     * 获取话题详情
     */
    @ApiOperation(value = "话题详情")
    @RequestMapping(value = "/topic/info", method = RequestMethod.GET)
    public CommonResult<CategoryTreeVo> geTopicInfo(Integer cid){
        return CommonResult.success(dynamicService.getTopic(cid));
    }

    /**
     * 查询文章详情
     * @param id Integer
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/article/info", method = RequestMethod.GET)
    public CommonResult<ArticleResponse> getArticleInfo(Integer id){
        return CommonResult.success(articleService.getVoByFront(id));
    }


    /**
     * 增加话题查看次数
     */
    @ApiOperation(value = "增加话题查看次数")
    @RequestMapping(value = "/topic/updateTopicVisit", method = RequestMethod.GET)
    public CommonResult<String> updateTopicVisit(Integer cid){
        if(dynamicService.updateTopicVisit(cid)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 获取话题动态列表
     */
    @ApiOperation(value = "话题动态列表")
    @RequestMapping(value = "/topic/listDynamic", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicVo>> getTopicListDynamic(@Validated DynamicRequest request, Integer page, Integer limit){
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(page);
        pageParamRequest.setLimit(limit);
        return CommonResult.success(CommonPage.restPage(dynamicService.getVoByHotFront(request,pageParamRequest)));
    }

    /**
     * 获取用户动态列表
     */
    @ApiOperation(value = "获取用户动态列表")
    @RequestMapping(value = "/topic/listDynamicByUid", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicVo>> getListDynamicByUid(Integer uid,Integer type, int page, int limit){
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(page);
        pageParamRequest.setLimit(limit);
        return CommonResult.success(CommonPage.restPage(dynamicService.getListDynamicByUid(uid,type,pageParamRequest)));
    }

    /**
     * （0-动态点赞，1-分享，2-收藏，3-评论点赞）
     * @param dynamicPraise 新增参数
     */
    @ApiOperation(value = "动态点赞,分享,收藏,评论点赞")
    @RequestMapping(value = "/topic/saveDynamicPraise", method = RequestMethod.POST)
    public CommonResult<String> saveDynamicPraise(@RequestBody @Validated DynamicPraise dynamicPraise){
        dynamicPraise.setImg(systemAttachmentService.clearPrefix(dynamicPraise.getImg()));
        if(dynamicService.dynamicPraise(dynamicPraise)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 用户取消点赞
     * @param dynamicPraise 新增参数
     */
    @ApiOperation(value = "用户取消点赞")
    @RequestMapping(value = "/topic/deleteDynamicPraise", method = RequestMethod.POST)
    public CommonResult<String> deleteDynamicPraise(@RequestBody @Validated DynamicPraise dynamicPraise){
        if(dynamicService.deleteDynamicPraise(dynamicPraise)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 用户取消收藏
     * @param dynamicPraise 新增参数
     */
    @ApiOperation(value = "用户取消收藏")
    @RequestMapping(value = "/topic/deleteDynamicCollet", method = RequestMethod.POST)
    public CommonResult<String> deleteDynamicCollet(@RequestBody @Validated DynamicPraise dynamicPraise){
        if(dynamicService.deleteDynamicCollet(dynamicPraise)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 用户取消评论点赞
     * @param dynamicPraise 新增参数
     */
    @ApiOperation(value = "用户取消评论点赞")
    @RequestMapping(value = "/topic/deleteCommentsPraise", method = RequestMethod.POST)
    public CommonResult<String> deleteCommentsPraise(@RequestBody @Validated DynamicPraise dynamicPraise){
        if(dynamicService.deleteCommentsPraise(dynamicPraise)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 用户是否关注
     * @param dynamicPraise 新增参数
     */
    @ApiOperation(value = "用户是否点赞")
    @RequestMapping(value = "/topic/isUserDynamicPraise", method = RequestMethod.POST)
    public CommonResult<Boolean> isUserDynamicPraise(@RequestBody @Validated DynamicPraise dynamicPraise){
        if(dynamicService.isUserDynamicPraise(dynamicPraise)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 用户关注
     * @param userAttention 新增参数
     */
    @ApiOperation(value = "用户关注")
    @RequestMapping(value = "/topic/saveUserAttention", method = RequestMethod.POST)
    public CommonResult<String> saveUserAttention(@RequestBody @Validated UserAttention userAttention){
        userAttention.setImg(systemAttachmentService.clearPrefix(userAttention.getImg()));
        if(dynamicService.userAttention(userAttention)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
    /**
     * 用户取消关注
     * @param userAttention 新增参数
     */
    @ApiOperation(value = "用户取消关注")
    @RequestMapping(value = "/topic/deleteUserAttention", method = RequestMethod.POST)
    public CommonResult<String> deleteUserAttention(@RequestBody @Validated UserAttention userAttention){
        if(dynamicService.deleteUserAttention(userAttention)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 用户是否关注
     * @param userAttention 新增参数
     */
    @ApiOperation(value = "用户是否关注")
    @RequestMapping(value = "/topic/isUserAttention", method = RequestMethod.POST)
    public CommonResult<Boolean> isUserAttention(@RequestBody @Validated UserAttention userAttention){
        if(dynamicService.isUserAttention(userAttention)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 发布动态
     * @param dynamicRequest 新增参数
     */
    @ApiOperation(value = "发布动态")
    @RequestMapping(value = "/topic/saveDynamic", method = RequestMethod.POST)
    public CommonResult<String> saveDynamic(@RequestBody @Validated DynamicRequest dynamicRequest){
        JSONObject jsonObject1 = userService.checkSpecialCharactersFun(dynamicRequest.getTitle());
        if(jsonObject1!=null){
            if(jsonObject1.get("errcode").toString().equals("87014")){
                throw new MallException("发布动态存在敏感内容，请检查后再发布！");
            }
        }
        Dynamic dynamic = new Dynamic();
        BeanUtils.copyProperties(dynamicRequest, dynamic);
        dynamic.setSliderImage(systemAttachmentService.clearPrefix(dynamic.getSliderImage()));
        if(dynamicService.saveDynamic(dynamic)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除动态
     * @param dynamicRequest 新增参数
     */
    @ApiOperation(value = "删除动态")
    @RequestMapping(value = "/topic/deleteDynamic", method = RequestMethod.POST)
    public CommonResult<String> deleteDynamic(@RequestBody @Validated DynamicRequest dynamicRequest){
        Dynamic dynamic = dynamicService.getById(dynamicRequest.getId());
        dynamic.setStatus(true);
        if(dynamicService.updateById(dynamic)){
            if(dynamic.getType() ==1){
                Category category = categoryService.getById(dynamic.getCid());
                if(category!=null){
                    if( category.getEqNum()==null){
                        category.setEqNum("0");
                    }else{
                        category.setEqNum(String.valueOf(Integer.valueOf(category.getEqNum())-1));
                    }
                    categoryService.updateById(category);
                }
            }
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询动态管理表信息
     * @param id Integer
     */
    @ApiOperation(value = "动态详情")
    @RequestMapping(value = "/topic/dynamicInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="动态ID")
    public CommonResult<DynamicVo> dynamicInfo(@RequestParam(value = "id") Integer id){
        Dynamic dynamic = dynamicService.getById(id);
        DynamicVo dynamicVo = new DynamicVo();
        if(dynamic!=null){
            BeanUtils.copyProperties(dynamic, dynamicVo);
            if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                dynamicVo.setSliderImage(dynamic.getSliderImage());
            }
            Integer uid = 0;
            if(dynamicVo.getAdminId()==0){
                User user = userService.getById(dynamicVo.getUid());
                if(user!=null){
                    dynamicVo.setUname(user.getNickname());
                    dynamicVo.setImg(user.getAvatar());
                    uid = user.getUid();
                }
            }else{
                Category category = categoryService.getById(dynamicVo.getUid());
                if(category!=null){
                    dynamicVo.setUname(category.getName());
                    dynamicVo.setImg(category.getExtra());
                    uid = category.getId();
                }
            }

            if(uid!=0){
                User user1 = userService.getInfo();
                if(user1 !=null){
                    try {
                        UserAttention userAttention = new UserAttention();
                        userAttention.setUid(user1.getUid());
                        userAttention.setDid(uid);
                        if(dynamic.getAdminId()==0){
                            userAttention.setType(0);
                        }else{
                            userAttention.setType(1);
                        }
                        dynamicVo.setIsGz(dynamicService.isUserAttention(userAttention));
                    }catch (Exception e){
                        dynamicVo.setIsGz(false);
                    }
                    try {
                        DynamicPraise dynamicPraise = new DynamicPraise();
                        dynamicPraise.setUid(user1.getUid());
                        dynamicPraise.setDid(dynamic.getId());
                        dynamicPraise.setType(0);
                        dynamicVo.setIsDz(dynamicService.isUserDynamicPraise(dynamicPraise));
                    }catch (Exception e){
                        dynamicVo.setIsDz(false);
                    }
                    try {
                        DynamicPraise dynamicPraise = new DynamicPraise();
                        dynamicPraise.setUid(user1.getUid());
                        dynamicPraise.setDid(dynamic.getId());
                        dynamicPraise.setType(2);
                        dynamicVo.setIsSc(dynamicService.isUserDynamicCollect(dynamicPraise));
                    }catch (Exception e){
                        dynamicVo.setIsSc(false);
                    }


                }else{
                    dynamicVo.setIsGz(false);
                    dynamicVo.setIsDz(false);
                    dynamicVo.setIsSc(false);
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
        }
        return CommonResult.success(dynamicVo);
    }

    /**
     * 新增动态评论管理表
     * @param dynamicComments 新增参数
     */
    @ApiOperation(value = "评论")
    @RequestMapping(value = "/topic/saveDynamicComments", method = RequestMethod.POST)
    public CommonResult<String> saveDynamicComments(@RequestBody @Validated DynamicComments dynamicComments){
        dynamicComments.setImg(systemAttachmentService.clearPrefix(dynamicComments.getImg()));
        if(dynamicService.dynamicComments(dynamicComments)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除动态评论
     * @param dynamicComments
     */
    @ApiOperation(value = "删除动态评论")
    @RequestMapping(value = "/topic/deleteDynamicComments", method = RequestMethod.POST)
    public CommonResult<String> deleteDynamicComments(@RequestBody @Validated DynamicComments dynamicComments){
        if(dynamicService.deleteDynamicComments(dynamicComments)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 分页显示动态评论管理表
     * @param request ArticleSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "示动态评论分页列表")
    @RequestMapping(value = "/topic/listDynamicComments", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicCommentsVo>> getCommentsList(@Validated DynamicComments request,
                                                                       @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getCommentsList(request, pageParamRequest)));
    }

    /**
     * 查询评论详情信息
     * @param id Integer
     */
    @ApiOperation(value = "评论详情")
    @RequestMapping(value = "/topic/commentsInfo", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="评论ID")
    public CommonResult<DynamicCommentsVo> commentsInfo(@RequestParam(value = "id") Integer id){
        return CommonResult.success( dynamicService.getCommentsInfo(id));
    }
    /**
     * 分页显示动态评论管理表
     * @param request ArticleSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "示动态评论分页列表")
    @RequestMapping(value = "/topic/listDynamicChildComments", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicCommentsVo>> getCommentsChildList(@Validated DynamicComments request,
                                                                            @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getCommentsChildList(request, pageParamRequest)));
    }

    /**
     * 关注用户分页列表
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "关注用户分页列表")
    @RequestMapping(value = "/topic/listGzUser", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserAttentionVo>> getListGzUser(@RequestParam(value = "id") Integer id, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getListGzUser(id,pageParamRequest)));
    }

    /**
     * 粉丝用户分页列表
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "粉丝用户分页列表")
    @RequestMapping(value = "/topic/listFsUser", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserAttentionVo>> getListFsUser(@RequestParam(value = "id") Integer id, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getListFsUser(id,pageParamRequest)));
    }

    /**
     * 论坛banner
     */
    @ApiOperation(value = "论坛banner")
    @RequestMapping(value = "/topic/banner", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> getTopicBanner(){
        return CommonResult.success(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_TOP_BANNER));
    }

    /**
     * 活动banner
     */
    @ApiOperation(value = "活动banner")
    @RequestMapping(value = "/acticity/banner", method = RequestMethod.GET)
    public CommonResult<List<HashMap<String, Object>>> getActivityBanner(){
        return CommonResult.success(systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_ACTICITY_BANNER));
    }

    /**
     * 获取活动列表
     * type类型ID
     */
    @ApiOperation(value = "活动列表")
    @RequestMapping(value = "/acticity/getlistActicity", method = RequestMethod.GET)
    public CommonResult<CommonPage<ActivityVo>> getlistActicity(Integer typer, Integer type, Integer pid, String content, int page, int limit){
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(page);
        pageParamRequest.setLimit(limit);
        return CommonResult.success(CommonPage.restPage(activityService.getlistActicity(typer,type,pid,content,pageParamRequest)));
    }

    /**
     * 用户活动列表
     */
    @ApiOperation(value = "活动列表")
    @RequestMapping(value = "/acticity/getlistActicityByUid", method = RequestMethod.GET)
    public CommonResult<CommonPage<ActivityVo>> getlistActicityByUid(Integer uid, int page, int limit){
        PageParamRequest pageParamRequest = new PageParamRequest();
        pageParamRequest.setPage(page);
        pageParamRequest.setLimit(limit);
        return CommonResult.success(CommonPage.restPage(activityService.getlistActicityByUid(uid,pageParamRequest)));
    }

    /**
     * 新增活动报名
     * @param activitySign 新增参数
     */
    @ApiOperation(value = "活动报名")
    @RequestMapping(value = "/acticity/saveActivitySign", method = RequestMethod.POST)
    public CommonResult<String> saveActivitySign(@RequestBody @Validated ActivitySign activitySign){
        activitySign.setImg(systemAttachmentService.clearPrefix(activitySign.getImg()));
        if(activityService.activitySign(activitySign)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 取消活动报名
     * @param activitySign 新增参数
     */
    @ApiOperation(value = "取消活动报名")
    @RequestMapping(value = "/acticity/deleteActivitySign", method = RequestMethod.POST)
    public CommonResult<String> deleteActivitySign(@RequestBody @Validated ActivitySign activitySign){
        if(activityService.activitySign_qx(activitySign)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 活动类型
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "活动类型")
    @RequestMapping(value = "/acticity/getListActivityType", method = RequestMethod.GET)
    public CommonResult<CommonPage<Category>> getListActivityType(@ModelAttribute CategorySearchRequest request, @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<Category> categoryCommonPage = CommonPage.restPage(categoryService.getList(request, pageParamRequest));
        return CommonResult.success(categoryCommonPage);
    }

    /**
     * 新增活动管理表
     * @param request 新增参数
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/acticity/saveActivity", method = RequestMethod.POST)
    public CommonResult<String> saveActivity(@RequestBody @Validated ActivityRequest request){
        Activity activity = new Activity();
        BeanUtils.copyProperties(request, activity);
        activity.setSliderImage(systemAttachmentService.clearPrefix(activity.getSliderImage()));
        if(activityService.saveActivity(activity)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 删除活动
     * @param request
     */
    @ApiOperation(value = "删除活动")
    @RequestMapping(value = "/acticity/deletActivity", method = RequestMethod.POST)
    public CommonResult<String> deletActivity(@RequestBody @Validated ActivityRequest request){
        boolean flag = false;
        Activity activity = activityService.getById(request.getId());
        if(activity!=null){
            if(activity.getActivityEnd().before(new Date())){
                activity.setStatus(true);
                if(activityService.updateById(activity)){
                    return CommonResult.success();
                }else{
                    return CommonResult.failed();
                }
            }else{
                List<ActivitySign> list = activityService.getListActivityBm(request.getId());
                if(list!=null && list.size()>0){
                    throw new MallException("活动已报名，不能删除！");
                }else{
                    activity.setStatus(true);
                    if(activityService.updateById(activity)){
                        return CommonResult.success();
                    }else{
                        return CommonResult.failed();
                    }
                }
            }
        }else{
            throw new MallException("活动不存在");
        }
    }
    /**
     * 查询活动管理表信息
     * @param id Integer
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/acticity/infoActivity", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="活动ID")
    public CommonResult<ActivityVo> getInfoActivity(@RequestParam(value = "id") Integer id){
        Activity activity = activityService.getById(id);
        if(activity.getVisit() ==null){
            activity.setVisit(0);
        }
        activity.setVisit(activity.getVisit()+1);
        activityService.updateById(activity);

        ActivityVo activityVo = new ActivityVo();
        if(activity!=null){
            BeanUtils.copyProperties(activity, activityVo);
            if(!StringUtils.isBlank(activity.getSliderImage()) ){
                activityVo.setSliderImages(MallUtil.jsonToListString(activity.getSliderImage()));
                activityVo.setSliderImage(activity.getSliderImage());
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

            Category category = categoryService.getById(activityVo.getPid());
            if(category!=null){
                activityVo.setPname(category.getName());
            }
            category = categoryService.getById(activityVo.getCid());
            if(category!=null){
                activityVo.setCname(category.getName());
            }
            User user = userService.getInfo();
            if(user==null){
                activityVo.setIsBm(false);
                activityVo.setIsSc(false);
                activityVo.setIsZan(false);
                activityVo.setIsGz(false);
            }else{
                LambdaQueryWrapper<ActivitySign> lambdaQueryWrapper1 = Wrappers.lambdaQuery();
                lambdaQueryWrapper1.eq(ActivitySign::getUid,user.getUid());
                lambdaQueryWrapper1.eq(ActivitySign::getDid,activityVo.getId());
                if(activitySignDao.selectOne(lambdaQueryWrapper1)==null){
                    activityVo.setIsBm(false);
                }else{
                    activityVo.setIsBm(true);
                }

                LambdaQueryWrapper<ActivityCollect> lambdaQueryWrapper2 = Wrappers.lambdaQuery();
                lambdaQueryWrapper2.eq(ActivityCollect::getUid,user.getUid());
                lambdaQueryWrapper2.eq(ActivityCollect::getDid,activityVo.getId());
                if(activityCollectDao.selectOne(lambdaQueryWrapper2)==null){
                    activityVo.setIsSc(false);
                }else{
                    activityVo.setIsSc(true);
                }

                LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(DynamicPraise::getUid, user.getUid());
                lambdaQueryWrapper.eq(DynamicPraise::getDid, activity.getId());
                lambdaQueryWrapper.eq(DynamicPraise::getType, 5);
                if(dynamicPraiseDao.selectOne(lambdaQueryWrapper)==null){
                    activityVo.setIsZan(false);
                }else{
                    activityVo.setIsZan(true);
                }

                LambdaQueryWrapper<UserAttention> lambdaQueryWrapper3 = Wrappers.lambdaQuery();
                lambdaQueryWrapper3.eq(UserAttention::getUid,user.getUid());
                lambdaQueryWrapper3.eq(UserAttention::getDid,activityVo.getUid());
                lambdaQueryWrapper3.eq(UserAttention::getType,1);
                if(userAttentionDao.selectOne(lambdaQueryWrapper3)==null){
                    activityVo.setIsGz(false);
                }else{
                    activityVo.setIsGz(true);
                }
            }
        }
        return CommonResult.success(activityVo);
    }

    /**
     * 新增活动收藏
     * @param activityCollect 新增参数
     */
    @ApiOperation(value = "新增活动收藏")
    @RequestMapping(value = "/acticity/saveActivityCollect", method = RequestMethod.POST)
    public CommonResult<String> saveActivityCollect(@RequestBody @Validated ActivityCollect activityCollect){
        activityCollect.setImg(systemAttachmentService.clearPrefix(activityCollect.getImg()));
        if(activityService.activityCollect(activityCollect)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
    /**
     * 取消活动收藏
     * @param activityCollect 新增参数
     */
    @ApiOperation(value = "取消活动收藏")
    @RequestMapping(value = "/acticity/deleteActivityCollect", method = RequestMethod.POST)
    public CommonResult<String> deleteActivityCollect(@RequestBody @Validated ActivityCollect activityCollect){
        if(activityService.activitySign_qx(activityCollect)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
    /**
     * 分页显示活动报名管理表
     * @param request ArticleSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/acticity/listActivitySign", method = RequestMethod.GET)
    public CommonResult<CommonPage<ActivitySign>> getlistActivitySign(@Validated ActivitySign request,
                                                                      @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(activityService.getlistActivitySign(request, pageParamRequest)));
    }
    /**
     * 查询报名信息
     * @param id Integer
     */
    @ApiOperation(value = "查询报名信息")
    @RequestMapping(value = "/acticity/activityBm", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="报名信息ID")
    public CommonResult<ActivitySign> getActivityBm(@RequestParam(value = "id") Integer id){
        return CommonResult.success(activityService.getActivityBm(id));
    }

    /**
     * 消息数据
     */
    @ApiOperation(value = "首页数据")
    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public CommonResult<MessageInfoResponse> getMessageInfo(){
        return CommonResult.success(dynamicService.getMessageInfo());
    }

    /**
     * 分页显示我的动态用户评论列表
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/message/listDynamicComments", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicCommentsVo>> getMsgListDynamicComments(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getMsgListDynamicComments(pageParamRequest)));
    }


    /**
     * 分页显示我的动态用户点赞
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/message/listDtDz", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicPraiseVo>> getMsgListDtDz(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getMsgListDtDz(pageParamRequest)));
    }


    /**
     * 分页显示我的动态用户评论点赞
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/message/listPlDz", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicPraiseVo>> getMsgListPlDz(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getMsgListPlDz(pageParamRequest)));
    }

    /**
     * 分页显示我的动态最新置顶动态
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/message/listDynamicTop", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicVo>> getMsgListDynamicTop(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getMsgLListDynamicTop(pageParamRequest)));
    }

    /**
     * 分页显示我的最新活动报名信息
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/message/listActivitySign", method = RequestMethod.GET)
    public CommonResult<CommonPage<ActivitySignVo>> getMsgListActivitySign(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getMsgListActivitySign(pageParamRequest)));
    }
}



