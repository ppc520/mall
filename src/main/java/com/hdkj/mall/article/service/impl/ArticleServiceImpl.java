package com.hdkj.mall.article.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
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
import com.hdkj.mall.article.model.*;
import com.hdkj.mall.article.request.ArticleSearchRequest;
import com.hdkj.mall.article.service.ArticleService;
import com.hdkj.mall.article.vo.ArticleVo;
import com.hdkj.mall.front.response.ArticleResponse;
import com.hdkj.mall.front.response.CommentResponse;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.system.service.SystemGroupDataService;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.service.UserService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.utils.MallUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.constants.Constants.ARTICLE_BANNER_LIMIT;

/**
* ArticleServiceImpl 接口实现
*/
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleDao, Article> implements ArticleService {

    private Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Resource
    private ArticleDao dao;

    @Resource
    private ArticleCommentDao articleCommentDao;

    @Resource
    private DynamicPraiseDao dynamicPraiseDao;

    @Resource
    private ActivityDao activityDao;
    @Resource
    private UserAttentionDao userAttentionDao;


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;
    @Autowired
    private UserService userService;
    /**
    * 列表
    * @param cid 文章分类id
    * @param pageParamRequest 分页类参数
    * @return PageInfo<Article>
    */
    @Override
    public PageInfo<ArticleResponse> getList(String cid, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Article::getCid, cid);
//        lambdaQueryWrapper.eq(Article::getType, 0);
        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getIsHot).orderByDesc(Article::getSort).orderByDesc(Article::getCreateTime);
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(articleList)) {
            return CommonPage.copyPageInfo(articlePage, CollUtil.newArrayList());
        }

        User user = userService.getInfo();
        List<ArticleResponse> responseList = articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            LambdaQueryWrapper<ArticleComments> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(ArticleComments::getArtid,articleResponse.getId());
            lambdaQuery.eq(ArticleComments::getType,1);
            articleResponse.setPl(articleCommentDao.selectCount(lambdaQuery));

            if(user==null){
                articleResponse.setIsZan(false);
            }else{
                LambdaQueryWrapper<DynamicPraise> lambdaQuery1 = Wrappers.lambdaQuery();
                lambdaQuery1.eq(DynamicPraise::getUid,user.getUid());
                lambdaQuery1.eq(DynamicPraise::getDid,articleResponse.getId());
                lambdaQuery1.eq(DynamicPraise::getType,4);
                if(dynamicPraiseDao.selectOne(lambdaQuery1)==null){
                    articleResponse.setIsZan(false);
                }else{
                    articleResponse.setIsZan(true);
                }
            }

            return articleResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(articlePage, responseList);
    }

    /**
     * 列表
     * @param cid 文章分类id
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    @Override
    public PageInfo<ArticleResponse> getList(String cid, Integer pid,Integer uid,PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if(cid!=null && !cid.equals("")){
//            lambdaQueryWrapper.eq(Article::getCid, cid);
            lambdaQueryWrapper.and(i -> i.or().eq(Article::getCid, cid)
                    .or().eq(Article::getCid, 0)
            );
        }
        if(pid!=null && pid!=0){
//            lambdaQueryWrapper.eq(Article::getPid, pid);
            lambdaQueryWrapper.and(i -> i.or().eq(Article::getPid, pid)
                    .or().eq(Article::getPid, 0)
            );
        }
//        lambdaQueryWrapper.eq(Article::getType, 0);
        if(!StringUtils.isBlank(pageParamRequest.getKeword())){
            lambdaQueryWrapper.and(i -> i.or().like(Article::getTitle, pageParamRequest.getKeword())
                    .or().like(Article::getAuthor, pageParamRequest.getKeword())
                    .or().like(Article::getSynopsis, pageParamRequest.getKeword())
                    .or().like(Article::getShareTitle, pageParamRequest.getKeword())
                    .or().like(Article::getShareSynopsis, pageParamRequest.getKeword())
            );
        }

        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getIsTop).
                orderByDesc(Article::getIsHot).
                orderByDesc(Article::getSort).
                orderByDesc(Article::getCreateTime);
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(articleList)) {
            return CommonPage.copyPageInfo(articlePage, CollUtil.newArrayList());
        }

        User user = userService.getById(uid);
        List<ArticleResponse> responseList = articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            if(articleResponse.getFileType() ==0){
                articleResponse.setImageInputs( JSON.parseArray(articleResponse.getImageInput(),String.class));
            }
            LambdaQueryWrapper<ArticleComments> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(ArticleComments::getArtid,articleResponse.getId());
            lambdaQuery.eq(ArticleComments::getType,1);
            articleResponse.setPl(articleCommentDao.selectCount(lambdaQuery));

            if(user ==null){
                articleResponse.setIsZan(false);
            }else{
                LambdaQueryWrapper<DynamicPraise> lambdaQuery1 = Wrappers.lambdaQuery();
                lambdaQuery1.eq(DynamicPraise::getUid, user.getUid());
                lambdaQuery1.eq(DynamicPraise::getDid, articleResponse.getId());
                lambdaQuery1.eq(DynamicPraise::getType, 4);
                if(dynamicPraiseDao.selectOne(lambdaQuery1)==null){
                    articleResponse.setIsZan(false);
                }else{
                    articleResponse.setIsZan(true);
                }
            }

            return articleResponse;
        }).collect(Collectors.toList());

//        List<HashMap<String, Object>> l =  systemGroupDataService.getListMapByGid(Constants.GROUP_DATA_ID_HOME_BOTTOM_BANNER);
//        System.out.println(l);
        return CommonPage.copyPageInfo(articlePage, responseList);
    }


    /**
     * 列表
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    @Override
    public PageInfo<ArticleResponse> getListByUid(Integer uid,PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if(!StringUtils.isBlank(pageParamRequest.getKeword())){
            lambdaQueryWrapper.and(i -> i.or().like(Article::getTitle, pageParamRequest.getKeword())
                    .or().like(Article::getAuthor, pageParamRequest.getKeword())
                    .or().like(Article::getSynopsis, pageParamRequest.getKeword())
                    .or().like(Article::getShareTitle, pageParamRequest.getKeword())
                    .or().like(Article::getShareSynopsis, pageParamRequest.getKeword())
            );
        }

        lambdaQueryWrapper.eq(Article::getUid, uid);
        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getCreateTime);
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(articleList)) {
            return CommonPage.copyPageInfo(articlePage, CollUtil.newArrayList());
        }

//        User user ;
//        if(uid==null || uid==0){
//            user = userService.getInfo();
//        }else{
//            user = userService.getById(uid);
//        }

        User user = userService.getInfo();

        List<ArticleResponse> responseList = articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            if(articleResponse.getFileType() ==0){
                articleResponse.setImageInputs( JSON.parseArray(articleResponse.getImageInput(),String.class));
            }
            LambdaQueryWrapper<ArticleComments> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(ArticleComments::getArtid,articleResponse.getId());
            lambdaQuery.eq(ArticleComments::getType,1);
            articleResponse.setPl(articleCommentDao.selectCount(lambdaQuery));

            if(user ==null){
                articleResponse.setIsZan(false);
            }else{
                LambdaQueryWrapper<DynamicPraise> lambdaQuery1 = Wrappers.lambdaQuery();
                lambdaQuery1.eq(DynamicPraise::getUid, user.getUid());
                lambdaQuery1.eq(DynamicPraise::getDid, articleResponse.getId());
                lambdaQuery1.eq(DynamicPraise::getType, 4);
                if(dynamicPraiseDao.selectOne(lambdaQuery1)==null){
                    articleResponse.setIsZan(false);
                }else{
                    articleResponse.setIsZan(true);
                }
            }

            return articleResponse;
        }).collect(Collectors.toList());

        return CommonPage.copyPageInfo(articlePage, responseList);
    }

    @Override
    public PageInfo<ArticleVo> getAdminList(ArticleSearchRequest request, PageParamRequest pageParamRequest) {
        Page<Article> articlePage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();

        if(StringUtils.isNotBlank(request.getCid())){
            lambdaQueryWrapper.eq(Article::getCid, request.getCid());
        }
        if(request.getType() !=null){
            lambdaQueryWrapper.eq(Article::getType, request.getType());
        }

        if(request.getPid()!=null && request.getPid()!=0){
            lambdaQueryWrapper.eq(Article::getPid, request.getPid());
        }

        if(request.getIsTop()!=null){
            lambdaQueryWrapper.eq(Article::getIsTop, request.getIsTop());
        }

        if(request.getIsHot()!=null){
            lambdaQueryWrapper.eq(Article::getIsHot, request.getIsHot());
        }


        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.or().like(Article::getTitle, request.getKeywords())
                    .or().like(Article::getAuthor, request.getKeywords())
                    .or().like(Article::getSynopsis, request.getKeywords())
                    .or().like(Article::getShareTitle, request.getKeywords())
                    .or().like(Article::getShareSynopsis, request.getKeywords()));
        }

        lambdaQueryWrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getIsHot).orderByDesc(Article::getSort).orderByDesc(Article::getCreateTime);
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);

        ArrayList<ArticleVo> articleVoArrayList = new ArrayList<>();
        if(articleList.size() < 1){
            return CommonPage.copyPageInfo(articlePage, articleVoArrayList);
        }
        for (Article article : articleList) {
            ArticleVo articleVo = new ArticleVo();
            BeanUtils.copyProperties(article, articleVo);
            if(!StringUtils.isBlank(article.getImageInput()) ){
                articleVo.setImageInput(MallUtil.jsonToListString(article.getImageInput()));
                articleVo.setImageInputs(article.getImageInput());
            }
            articleVoArrayList.add(articleVo);
        }

        return CommonPage.copyPageInfo(articlePage, articleVoArrayList);
    }

    /**
     * 关联产品
     * @param id Integer
     * @param productId 产品id
     * @author Mr.Zhou
     * @since 2022-04-18
     * @return bool
     */
    @Override
    public boolean update(Integer id, Integer productId) {
        Article article = new Article();
        article.setId(id);
        article.setProductId(productId);
        updateById(article);
        return true;
    }

    /**
     * 查询文章详情
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-18
     * @return ArticleVo
     */
    @Override
    public ArticleResponse getVoByFront(Integer id) {
        Article article = getById(id);
        if (ObjectUtil.isNull(article)) {
            throw new MallException("文章不存在");
        }

        if(article.getStatus()){
            throw new MallException("文章不存在");
        }

        ArticleResponse articleResponse = new ArticleResponse();
        BeanUtils.copyProperties(article, articleResponse);
        if(articleResponse.getFileType() ==0){
            articleResponse.setImageInputs( JSON.parseArray(articleResponse.getImageInput(),String.class));
        }
        try {
            String visit = Optional.ofNullable(article.getVisit()).orElse("0");
            if(article.getVisit()==null || article.getVisit().equals("")){
                visit = "0";
            }
            int num = Integer.parseInt(visit) + 1;
            article.setVisit(String.valueOf(num));
            dao.updateById(article);
            articleResponse.setVisit(article.getVisit());

            LambdaQueryWrapper<ArticleComments> lambdaQuery = Wrappers.lambdaQuery();
            lambdaQuery.eq(ArticleComments::getArtid,articleResponse.getId());
            lambdaQuery.eq(ArticleComments::getType,1);
            articleResponse.setPl(articleCommentDao.selectCount(lambdaQuery));


            User user = userService.getInfo();
            if(user!=null){
                LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
                lambdaQueryWrapper.eq(DynamicPraise::getUid, user.getUid());
                lambdaQueryWrapper.eq(DynamicPraise::getDid, articleResponse.getId());
                lambdaQueryWrapper.eq(DynamicPraise::getType, 4);
                if(dynamicPraiseDao.selectOne(lambdaQueryWrapper)==null){
                    articleResponse.setIsZan(false);
                }else{
                    articleResponse.setIsZan(true);
                }

                LambdaQueryWrapper<UserAttention> lambdaQueryWrapper3 = Wrappers.lambdaQuery();
                lambdaQueryWrapper3.eq(UserAttention::getUid,user.getUid());
                lambdaQueryWrapper3.eq(UserAttention::getDid,articleResponse.getUid());
                lambdaQueryWrapper3.eq(UserAttention::getType,1);
                if(userAttentionDao.selectOne(lambdaQueryWrapper3)==null){
                    articleResponse.setIsGz(false);
                }else{
                    articleResponse.setIsGz(true);
                }
            }else{
                articleResponse.setIsGz(false);
                articleResponse.setIsZan(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("查看文章详情，更新浏览量失败，errorMsg = " + e.getMessage());
        }
        return articleResponse;
    }

    /**
     * 获取移动端banner列表
     * @return List<Article>
     */
    @Override
    public List<Article> getBannerList() {
        // 根据配置控制banner的数量
        String articleBannerLimitString = systemConfigService.getValueByKey(ARTICLE_BANNER_LIMIT);
        int articleBannerLimit = Integer.parseInt(articleBannerLimitString);

        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(Article::getId, Article::getImageInput);
        lambdaQueryWrapper.eq(Article::getIsBanner, true);
        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getSort);
        lambdaQueryWrapper.last(" limit " + articleBannerLimit);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 获取移动端热门列表
     * @return List<ArticleResponse>
     */
    @Override
    public List<ArticleResponse> getHotList() {
        LambdaQueryWrapper<Article> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(Article::getId, Article::getImageInput, Article::getTitle, Article::getCreateTime);
        lambdaQueryWrapper.eq(Article::getIsHot, true);
        lambdaQueryWrapper.eq(Article::getType, 0);
        lambdaQueryWrapper.eq(Article::getHide, false);
        lambdaQueryWrapper.eq(Article::getStatus, false);
        lambdaQueryWrapper.orderByDesc(Article::getSort);
        lambdaQueryWrapper.last(" limit 20");
        List<Article> articleList = dao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(articleList)) {
            return CollUtil.newArrayList();
        }
        List<ArticleResponse> responseList = articleList.stream().map(e -> {
            ArticleResponse articleResponse = new ArticleResponse();
            BeanUtils.copyProperties(e, articleResponse);
            return articleResponse;
        }).collect(Collectors.toList());
        return responseList;
    }

    /**
     * 获取文章分类列表(移动端)
     * @return List<Category>
     */
    @Override
    public List<Category> getCategoryList() {
        return categoryService.findArticleCategoryList();
    }

    /**
     * 列表
     * @param artid 文章id
     * @param pageParamRequest 分页类参数
     * @return PageInfo<Article>
     */
    @Override
    public PageInfo<CommentResponse> getPLList(Integer artid, Integer type, PageParamRequest pageParamRequest) {
        Page<ArticleComments> commentsPage = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        LambdaQueryWrapper<ArticleComments> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ArticleComments::getArtid, artid);
        lambdaQueryWrapper.eq(ArticleComments::getType, type);
        lambdaQueryWrapper.orderByDesc(ArticleComments::getCreateTime);
        List<ArticleComments> commentsList = articleCommentDao.selectList(lambdaQueryWrapper);
        if (CollUtil.isEmpty(commentsList)) {
            return CommonPage.copyPageInfo(commentsPage, CollUtil.newArrayList());
        }
        List<CommentResponse> responseList = commentsList.stream().map(e -> {
            CommentResponse commentResponse = new CommentResponse();
            BeanUtils.copyProperties(e, commentResponse);
            return commentResponse;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(commentsPage, responseList);
    }

    /**
     * 新增评论管理表
     * @param articleComments
     * @return bool
     */
    @Override
    public boolean savePL(ArticleComments articleComments) {
        if(articleCommentDao.insert(articleComments)>0){
            return true;
        }else{
            return false;
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
            //动态点赞
            LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
            lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
            lambdaQueryWrapper.eq(DynamicPraise::getType, dynamicPraise.getType());
            DynamicPraise dynamicPraise1 = dynamicPraiseDao.selectOne(lambdaQueryWrapper);
            if(dynamicPraise1 ==null){
                if(dynamicPraiseDao.insert(dynamicPraise)>0){
                    if(dynamicPraise.getType()==4){
                        Article article  = getById(dynamicPraise.getDid());
                        if(article !=null){
                            if(article.getZan()==null){
                                article.setZan(0);
                            }
                            article.setZan(article.getZan()+1);
                            dao.updateById(article);
                        }
                    }else if(dynamicPraise.getType()==5){
                        Activity Activity  = activityDao.selectById(dynamicPraise.getDid());
                        if(Activity !=null){
                            if(Activity.getZan()==null){
                                Activity.setZan(0);
                            }
                            Activity.setZan(Activity.getZan()+1);
                            activityDao.updateById(Activity);
                        }
                    }

                    return true;
                }else{
                    throw new MallException("点赞失败!");
                }

            }else{
                throw new MallException("已经点赞!");
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
            if(dynamicPraise.getDid() ==null ||dynamicPraise.getDid() ==0
                    || dynamicPraise.getUid()==null|| dynamicPraise.getUid()==0){
                throw new MallException("参数错误!");
            }

            LambdaQueryWrapper<DynamicPraise> lambdaQueryWrapper = Wrappers.lambdaQuery();
            lambdaQueryWrapper.eq(DynamicPraise::getUid, dynamicPraise.getUid());
            lambdaQueryWrapper.eq(DynamicPraise::getDid, dynamicPraise.getDid());
            lambdaQueryWrapper.eq(DynamicPraise::getType, dynamicPraise.getType());
            if(dynamicPraiseDao.delete(lambdaQueryWrapper)>0){
                if(dynamicPraise.getType()==4){
                    Article article  = getById(dynamicPraise.getDid());
                    if(article !=null){
                        if(article.getZan()==null){
                            article.setZan(0);
                        }
                        if(article.getZan()>0){
                            article.setZan(article.getZan()-1);
                            dao.updateById(article);
                        }
                    }
                }else if(dynamicPraise.getType()==5){
                    Activity Activity  = activityDao.selectById(dynamicPraise.getDid());
                    if(Activity !=null){
                        if(Activity.getZan()==null){
                            Activity.setZan(0);
                        }
                        Activity.setZan(Activity.getZan()-1);
                        activityDao.updateById(Activity);
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
}

