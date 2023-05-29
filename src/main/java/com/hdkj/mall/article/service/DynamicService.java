package com.hdkj.mall.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.article.model.Dynamic;
import com.hdkj.mall.article.model.DynamicComments;
import com.hdkj.mall.article.model.DynamicPraise;
import com.hdkj.mall.article.model.UserAttention;
import com.hdkj.mall.article.request.DynamicRequest;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.front.response.MessageInfoResponse;
import com.hdkj.mall.article.vo.*;

import java.util.List;

/**
*  DynamicService 接口
*/
public interface DynamicService extends IService<Dynamic> {

    PageInfo<DynamicVo> getAdminList(DynamicRequest request, PageParamRequest pageParamRequest);

    /**
     * 动态详情
     * @param id 文章id
     * @return ArticleVo
     */
    DynamicVo getVoByFront(Integer id);

    /**
     * 评论详情
     * @param id 文章id
     * @return ArticleVo
     */
    DynamicCommentsVo getCommentsInfo(Integer id);
    /**
     * 话题动态
     */
    PageInfo<DynamicVo> getVoByHotFront(DynamicRequest request, PageParamRequest pageParamRequest);
    /**
     * 获取用户动态列表
     */
    PageInfo<DynamicVo> getListDynamicByUid(Integer uid,Integer type, PageParamRequest pageParamRequest);

    /**
     * 获取话题详情
     * @param cid
     * @return
     */
    CategoryTreeVo getTopic(Integer cid);

    /**
     * 增加话题查看次数
     */
    Boolean updateTopicVisit(Integer cid);

    /**
     * 获取移动端热门列表
     * @return List<ArticleResponse>
     */
    List<DynamicVo> getHotList();

    /**
     * 获取首页话题列表
     * @return List<Category>
     */
    List<Category> getHomeTopicList();


    /**
     * 话题列表
     * @return List<Category>
     */
    PageInfo<Category> getTopicList(PageParamRequest pageParamRequest);


    /**
     * 话题列表
     * @return List<Category>
     */
    PageInfo<CategoryTreeVo> getMyTopicList(Integer uid, PageParamRequest pageParamRequest);

    /**
     * 添加动态
     * @return List<Categoy>
     */
    Boolean saveDynamic(Dynamic dynamic);


    /**
     * 动态评论
     */
    Boolean dynamicComments(DynamicComments dynamicComments);


    /**
     * 删除评论
     */
    Boolean deleteDynamicComments(DynamicComments dynamicComments);

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


    /**
     * 用户取消收藏
     * @return List<Category>
     */
    Boolean deleteDynamicCollet(DynamicPraise dynamicPraise);
    /**
     * 用户取消评论点赞
     * @return List<Category>
     */
    Boolean deleteCommentsPraise(DynamicPraise dynamicPraise);

    /**
     * 用户关注
     * @return List<Category>
     */
    Boolean userAttention(UserAttention UserAttention);

    /**
     * 用户取消关注
     * @return List<Category>
     */
    Boolean deleteUserAttention(UserAttention UserAttention);


    /**
     * 用户是否关注
     */
    Boolean isUserAttention(UserAttention UserAttention);


    /**
     * 用户是否点赞
     */
    Boolean isUserDynamicPraise(DynamicPraise dynamicPraise);

    /**
     * 用户是否收藏
     */
    Boolean isUserDynamicCollect(DynamicPraise dynamicPraise);

    /**
     * 评论列表
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<DynamicCommentsVo> getCommentsList(DynamicComments request, PageParamRequest pageParamRequest);

    /**
     * 评论列表
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<DynamicCommentsVo> getCommentsChildList(DynamicComments request, PageParamRequest pageParamRequest);

    /**
     * 点赞-转发列表  两个接口
     * @param request
     * @param pageParamRequest
     * @return
     */
    PageInfo<DynamicPraise> getPraiseList(DynamicPraise request, PageParamRequest pageParamRequest);


    /**
     * 关注用户分页列表
     * @param pageParamRequest
     * @return
     */
    PageInfo<UserAttentionVo> getListGzUser(Integer uid, PageParamRequest pageParamRequest);


    /**
     * 粉丝用户分页列表
     * @param pageParamRequest
     * @return
     */
    PageInfo<UserAttentionVo> getListFsUser(Integer uid, PageParamRequest pageParamRequest);


    /**
     * 用户消息
     * @return IndexInfoResponse
     */
    MessageInfoResponse getMessageInfo();


    PageInfo<DynamicCommentsVo> getMsgListDynamicComments(PageParamRequest pageParamRequest);

    PageInfo<DynamicPraiseVo> getMsgListDtDz(PageParamRequest pageParamRequest);

    PageInfo<DynamicPraiseVo> getMsgListPlDz(PageParamRequest pageParamRequest);

    PageInfo<DynamicVo> getMsgLListDynamicTop(PageParamRequest pageParamRequest);

    PageInfo<ActivitySignVo> getMsgListActivitySign(PageParamRequest pageParamRequest);

}
