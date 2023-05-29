package com.hdkj.mall.front.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.hdkj.mall.article.model.Article;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.article.model.ArticleComments;
import com.hdkj.mall.article.model.DynamicPraise;
import com.hdkj.mall.article.service.ArticleService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.front.request.ArticleCommentRequest;
import com.hdkj.mall.front.response.ArticleResponse;
import com.hdkj.mall.front.response.CommentResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 文章

 */
@Slf4j
@RestController("ArticleFrontController")
@RequestMapping("api/front/article")
@Api(tags = "文章")

public class ArticleController {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 分页列表
     * @return
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list/{cid}/{pid}/{uid}", method = RequestMethod.GET)
    public CommonResult<CommonPage<ArticleResponse>> getList(@PathVariable(name="cid") String cid,
                                                             @PathVariable(name="pid") Integer pid,
                                                             @PathVariable(name="uid") Integer uid,
                                                             @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(articleService.getList(cid,pid, uid,pageParamRequest)));
    }


    /**
     * 分页列表
     * @return
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/listByUid/{uid}", method = RequestMethod.GET)
    public CommonResult<CommonPage<ArticleResponse>> getListByUid(
                                                             @PathVariable(name="uid") Integer uid,
                                                             @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(articleService.getListByUid(uid,pageParamRequest)));
    }

    /**
     * 热门列表
     */
    @ApiOperation(value = "热门列表")
    @RequestMapping(value = "/hot/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<ArticleResponse>> getHotList(){
        return CommonResult.success(CommonPage.restPage(articleService.getHotList()));
    }

    /**
     * 轮播列表
     */
    @ApiOperation(value = "轮播列表")
    @RequestMapping(value = "/banner/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<Article>> getList(){
        return CommonResult.success(CommonPage.restPage(articleService.getBannerList()));
    }

    /**
     * 文章分类列表
     */
    @ApiOperation(value = "文章分类列表")
    @RequestMapping(value = "/category/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<Category>> categoryList(){
        return CommonResult.success(CommonPage.restPage(articleService.getCategoryList()));
    }

    /**
     * 查询文章详情
     * @param id Integer
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="文章ID")
    public CommonResult<ArticleResponse> info(@RequestParam(value = "id") Integer id){
        return CommonResult.success(articleService.getVoByFront(id));
   }

    /**
     * 点赞文章管理表
     * @param id Integer
     */
    @ApiOperation(value = "点赞")
    @RequestMapping(value = "/zan", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="文章ID")
    public CommonResult<String> zan(@RequestParam(value = "id") Integer id){
        Article article = articleService.getById(id);
        article.setZan(article.getZan()==null?0:article.getZan()+1);
        if(articleService.updateById(article)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
    /**
     * 取消点赞文章管理表
     * @param id Integer
     */
    @ApiOperation(value = "取消点赞")
    @RequestMapping(value = "/qxzan", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="文章ID")
    public CommonResult<String> qxzan(@RequestParam(value = "id") Integer id){
        Article article = articleService.getById(id);
        article.setZan(article.getZan()==null||article.getZan()==0?0:article.getZan()-1);
        if(articleService.updateById(article)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * （0-动态点赞，1-分享，2-收藏，3-评论点赞）
     * @param dynamicPraise 新增参数
     */
    @ApiOperation(value = "文章点赞")
    @RequestMapping(value = "/saveZan", method = RequestMethod.POST)
    public CommonResult<String> saveZan(@RequestBody @Validated DynamicPraise dynamicPraise){
        dynamicPraise.setImg(systemAttachmentService.clearPrefix(dynamicPraise.getImg()));
        if(articleService.dynamicPraise(dynamicPraise)){
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
    @RequestMapping(value = "/deleteZan", method = RequestMethod.POST)
    public CommonResult<String> deleteZan(@RequestBody @Validated DynamicPraise dynamicPraise){
        if(articleService.deleteDynamicPraise(dynamicPraise)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 获取评论列表
     * @return
     */
    @ApiOperation(value = "获取评论列表")
    @RequestMapping(value = "/list/pl/{artid}/{type}", method = RequestMethod.GET)
    public CommonResult<CommonPage<CommentResponse>> getAnswerList(@PathVariable(name="artid") Integer artid, @PathVariable(name="type")  Integer type,
                                                                   @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(articleService.getPLList(artid,type,pageParamRequest)));
    }


    /**
     * 评论数据保存
     * @return
     */
    @ApiOperation(value = "评论数据保存")
    @RequestMapping(value = "/save/pl", method = RequestMethod.POST)
    public CommonResult<String> savePL(@RequestBody @Validated ArticleCommentRequest articleCommentRequest){
        ArticleComments articleComments = new ArticleComments();
        BeanUtils.copyProperties(articleCommentRequest, articleComments);

        if(articleService.savePL(articleComments)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }
}



