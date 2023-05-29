package com.hdkj.mall.article.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.hdkj.mall.article.model.*;
import com.hdkj.mall.article.request.ActivityRequest;
import com.hdkj.mall.article.service.DynamicService;
import com.hdkj.mall.article.vo.DynamicCommentsVo;
import com.hdkj.mall.article.vo.DynamicVo;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.article.service.ActivityService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.hdkj.mall.user.model.User;
import com.utils.MallUtil;
import com.hdkj.mall.article.request.DynamicRequest;
import com.hdkj.mall.article.vo.ActivityVo;
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


/**
 * 动态管理表 前端控制器
 */
@Slf4j
@RestController
@RequestMapping("api/admin/dynamic")
@Api(tags = "动态管理")
public class DynamicController {

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;


    /**
     * 分页显示动态管理表
     * @param request ArticleSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiImplicitParam(name="keywords", value="搜索关键字")
    public CommonResult<CommonPage<DynamicVo>> getList(@Validated DynamicRequest request,
                                                       @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getAdminList(request, pageParamRequest)));
    }

    /**
     * 新增动态管理表
     * @param dynamicRequest 新增参数
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> saveDynamic(@RequestBody @Validated DynamicRequest dynamicRequest){
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
     * 删除动态管理表
     * @param id Integer
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="动态ID")
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(dynamicService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 修改动态管理表
     * @param id integer id
     * @param dynamicRequest 修改参数
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name="id", value="动态ID")
    public CommonResult<String> update(@RequestParam Integer id, @RequestBody @Validated DynamicRequest dynamicRequest){
        Dynamic dynamic = new Dynamic();
        BeanUtils.copyProperties(dynamicRequest, dynamic);
        if(dynamicRequest.getType()==0){
            dynamic.setCid(null);
        }
        dynamic.setSliderImage(systemAttachmentService.clearPrefix(dynamic.getSliderImage()));
        dynamic.setId(id);
        dynamic.setContent(systemAttachmentService.clearPrefix(dynamic.getContent()));

        if(dynamicService.updateById(dynamic)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询动态管理表信息
     * @param id Integer
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="动态ID")
    public CommonResult<DynamicVo> info(@RequestParam(value = "id") Integer id){
        Dynamic dynamic = dynamicService.getById(id);
        DynamicVo dynamicVo = new DynamicVo();
        if(dynamic!=null){
            BeanUtils.copyProperties(dynamic, dynamicVo);
            if(!StringUtils.isBlank(dynamic.getSliderImage()) ){
                dynamicVo.setSliderImages(MallUtil.jsonToListString(dynamic.getSliderImage()));
                dynamicVo.setSliderImage(dynamic.getSliderImage());
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
        }
        return CommonResult.success(dynamicVo);
   }

    /**
     * 新增动态评论管理表
     * @param dynamicComments 新增参数
     */
    @ApiOperation(value = "评论")
    @RequestMapping(value = "/saveDynamicComments", method = RequestMethod.POST)
    public CommonResult<String> dynamicComments(@RequestBody @Validated DynamicComments dynamicComments){
        if(dynamicService.dynamicComments(dynamicComments)){
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
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/listComments", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicCommentsVo>> getCommentsList(@Validated DynamicComments request,
                                                                       @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getCommentsList(request, pageParamRequest)));
    }

    /**
     * 分页显示动态点赞管理表
     * @param request ArticleSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/listPraise", method = RequestMethod.GET)
    public CommonResult<CommonPage<DynamicPraise>> getPraiseList(@Validated DynamicPraise request,
                                                                 @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(dynamicService.getPraiseList(request, pageParamRequest)));
    }



    /**
     * 分页显示活动管理表
     * @param request ArticleSearchRequest 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/listActivity", method = RequestMethod.GET)
    @ApiImplicitParam(name="keywords", value="搜索关键字")
    public CommonResult<CommonPage<ActivityVo>> getListActivity(@Validated ActivityRequest request,
                                                                @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(activityService.getAdminList(request, pageParamRequest)));
    }

    /**
     * 新增活动管理表
     * @param request 新增参数
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/saveActivity", method = RequestMethod.POST)
    public CommonResult<String> saveActivity(@RequestBody @Validated ActivityRequest request){
        Activity activity = new Activity();
        BeanUtils.copyProperties(request, activity);
        activity.setUrl(systemAttachmentService.clearPrefix(request.getUrl()));
        activity.setImage(systemAttachmentService.clearPrefix(activity.getImage()));
        activity.setSliderImage(systemAttachmentService.clearPrefix(activity.getSliderImage()));
        if(activityService.saveActivity(activity)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除活动管理表
     * @param id Integer
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/deleteActivity", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="动态ID")
    public CommonResult<String> deleteActivity(@RequestParam(value = "id") Integer id){
        if(activityService.removeById(id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }


    /**
     * 修改活动管理表
     * @param id integer id
     * @param request 修改参数
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/updateActivity", method = RequestMethod.POST)
    @ApiImplicitParam(name="id", value="活动ID")
    public CommonResult<String> updateActivity(@RequestParam Integer id, @RequestBody @Validated ActivityRequest request){
        Activity activity = new Activity();
        BeanUtils.copyProperties(request, activity);
        activity.setUrl(systemAttachmentService.clearPrefix(request.getUrl()));
        activity.setImage(systemAttachmentService.clearPrefix(activity.getImage()));
        activity.setSliderImage(systemAttachmentService.clearPrefix(activity.getSliderImage()));
        activity.setId(id);

        if(activityService.updateById(activity)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询活动管理表信息
     * @param id Integer
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/infoActivity", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="活动ID")
    public CommonResult<ActivityVo> infoActivity(@RequestParam(value = "id") Integer id){
        Activity activity = activityService.getById(id);
        ActivityVo activityVo = new ActivityVo();
        if(activity!=null){
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
        }
        return CommonResult.success(activityVo);
    }

    /**
     * 活动审核
     * @param id integer id
     * @param request 修改参数
     */
    @ApiOperation(value = "活动审核")
    @RequestMapping(value = "/checkActivity", method = RequestMethod.POST)
    @ApiImplicitParam(name="id", value="活动ID")
    public CommonResult<String> checkActivity(@RequestParam Integer id, @RequestBody @Validated ActivityRequest request){
        Activity activity = new Activity();
        activity.setIsCheck(request.getIsCheck());
        activity.setId(id);

        if(activityService.updateById(activity)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 新增活动报名
     * @param activitySign 新增参数
     */
    @ApiOperation(value = "活动报名")
    @RequestMapping(value = "/saveActivitySign", method = RequestMethod.POST)
    public CommonResult<String> saveActivitySign(@RequestBody @Validated ActivitySign activitySign){
        if(activityService.activitySign(activitySign)){
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
    @RequestMapping(value = "/listActivitySign", method = RequestMethod.GET)
    public CommonResult<CommonPage<ActivitySign>> getlistActivitySign(@Validated ActivitySign request,
                                                                      @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(activityService.getlistActivitySign(request, pageParamRequest)));
    }
}



