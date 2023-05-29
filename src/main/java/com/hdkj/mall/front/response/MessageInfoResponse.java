package com.hdkj.mall.front.response;

import com.hdkj.mall.article.model.ActivitySign;
import com.hdkj.mall.article.model.Dynamic;
import com.hdkj.mall.article.model.DynamicComments;
import com.hdkj.mall.article.model.DynamicPraise;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 消息Response
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="MessageInfoResponse对象", description="用户消息返回数据")
public class MessageInfoResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "新闻消息")
    private HashMap<String, Object> roll;

    @ApiModelProperty(value = "最新用户评论动态")
    private List<DynamicComments> dynamicComments;

    @ApiModelProperty(value = "最新用户点赞动态")
    private List<DynamicPraise> dtDz;

    @ApiModelProperty(value = "最新用户点赞评论")
    private List<DynamicPraise> plDz;

    @ApiModelProperty(value = "用户最新置顶动态")
    private Dynamic dynamicTop;

    @ApiModelProperty(value = "用户最新活动报名")
    private List<ActivitySign> activitySign;

}
