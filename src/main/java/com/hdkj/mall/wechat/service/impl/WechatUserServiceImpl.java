package com.hdkj.mall.wechat.service.impl;

import com.exception.MallException;
import com.hdkj.mall.article.model.Article;
import com.hdkj.mall.user.model.UserToken;
import com.hdkj.mall.wechat.service.WechatUserService;
import com.utils.MallUtil;
import com.hdkj.mall.article.service.ArticleService;
import com.hdkj.mall.user.service.UserTokenService;
import com.hdkj.mall.wechat.service.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 微信用户表 服务实现类
 */
@Service
public class WechatUserServiceImpl implements WechatUserService {
    @Autowired
    private ArticleService articleService;

    @Lazy
    @Autowired
    private WeChatService weChatService;

    @Autowired
    private UserTokenService userTokenService;


    /**
     * 消息推送
     * @param userId 用户id
     * @param newsId 图文消息id
     * @author Mr.Zhou
     * @since 2022-04-11
     * @return Boolean
     */
    @Override
    public void push(String userId, Integer newsId) {
        //检查文章是否存在
        Article article = articleService.getById(newsId);
        if(article == null){
            throw new MallException("你选择的文章不存在！");
        }
        List<Integer> userIdList = MallUtil.stringToArray(userId);
        List<UserToken> userList = userTokenService.getList(userIdList);
        if(null == userList){
            throw new MallException("没有用户关注微信号");
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("msgtype", "news");

        HashMap<String, Object> articleInfo = new HashMap<>();
        ArrayList<Object> articleList = new ArrayList<>();

        HashMap<String, String> articleInfoItem = new HashMap<>();

        for (UserToken userToken : userList) {
            map.put("touser", userToken.getToken());

            articleInfoItem.put("title", article.getTitle());
            articleInfoItem.put("description", article.getSynopsis());
            articleInfoItem.put("url", article.getUrl()); //前端地址或者三方地址
            articleInfoItem.put("picurl", article.getImageInput());
            articleList.add(articleInfoItem);
            articleInfo.put("articles", articleList);
            map.put("news", articleInfo);
            weChatService.pushKfMessage(map);
        }
    }

}
