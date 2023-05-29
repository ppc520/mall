package com.hdkj.mall.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.article.model.Article;
import com.hdkj.mall.article.model.DynamicPraise;
import com.hdkj.mall.article.request.ArticleSearchRequest;
import com.hdkj.mall.article.vo.ArticleVo;
import com.hdkj.mall.front.response.CommentResponse;
import com.hdkj.mall.article.model.ArticleComments;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.front.response.ArticleResponse;

import java.util.List;

/**
*  ArticleService 接口
*/
public interface ArticleService extends IService<Article> {

    /**
     * 文章列表
     * @param cid 文章分类id
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    PageInfo<ArticleResponse> getList(String cid, PageParamRequest pageParamRequest);
    /**
     * 文章列表
     * @param cid 文章分类id
     * @param pid 学习ID
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    PageInfo<ArticleResponse> getList(String cid,Integer pid,Integer uid,PageParamRequest pageParamRequest);

    PageInfo<ArticleResponse> getListByUid(Integer uid,PageParamRequest pageParamRequest);

    PageInfo<ArticleVo> getAdminList(ArticleSearchRequest request, PageParamRequest pageParamRequest);

    boolean update(Integer id, Integer productId);

    /**
     * 文章详情
     * @param id 文章id
     * @return ArticleVo
     */
    ArticleResponse getVoByFront(Integer id);

    /**
     * 获取移动端banner列表
     * @return List<Article>
     */
    List<Article> getBannerList();

    /**
     * 获取移动端热门列表
     * @return List<ArticleResponse>
     */
    List<ArticleResponse> getHotList();

    /**
     * 获取文章分类列表
     * @return List<Category>
     */
    List<Category> getCategoryList();


    PageInfo<CommentResponse> getPLList(Integer artid, Integer type, PageParamRequest pageParamRequest);

    /**
     * 新增评论管理表
     * @param comments
     * @return
     */
    boolean savePL(ArticleComments comments);


    /**
     * 动态点赞-转发 两个接口
     * @return List<Category>
     */
    Boolean dynamicPraise(DynamicPraise dynamicPraise);

    /**
     * 用户取消点赞
     * @return List<Category>
     */
    Boolean deleteDynamicPraise(DynamicPraise dynamicPraise);

}
