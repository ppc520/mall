package com.hdkj.mall.user.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务订单评论表
 */
@Data
@ApiModel(value="UserLjhsOrderReplyResponse对象", description="服务订单评论表")
public class UserLjhsOrderReplyResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "用户id")
    private Integer uid;

    @ApiModelProperty(value = "类型id")
    private Integer type;

    @ApiModelProperty(value = "评论类型(洗衣、回收、维修）")
    private String replyType;

    @ApiModelProperty(value = "质量分数")
    private Integer qualityScore;

    @ApiModelProperty(value = "服务分数")
    private Integer serviceScore;

    @ApiModelProperty(value = "评论内容")
    private String comment;

    @ApiModelProperty(value = "评论图片")
    private String pics;

    @ApiModelProperty(value = "管理员回复内容")
    private String merchantReplyContent;

    @ApiModelProperty(value = "管理员回复时间")
    private Date merchantReplyTime;

    @ApiModelProperty(value = "是否删除，0未删除1已删除")
    private Integer isDel;

    @ApiModelProperty(value = "是否回复，0未回复1已回复")
    private Integer isReply;

    @ApiModelProperty(value = "用户名称")
    private String nickname;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date update_time;

    @ApiModelProperty(value = "商品分数")
    private Integer score;

}
