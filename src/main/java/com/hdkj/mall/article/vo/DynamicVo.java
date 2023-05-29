package com.hdkj.mall.article.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 动态管理表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DynamicVo对象", description="动态管理表")
public class DynamicVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "动态管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "学校ID")
    private Integer pid;

    @ApiModelProperty(value = "学校名称")
    private String pname;

    @ApiModelProperty(value = "显示方式")
    private Integer flag;

    @ApiModelProperty(value = "动态类型（0-广告，1-帖子）")
    private Integer type;

    @ApiModelProperty(value = "上传图片还是视频(0-图片，1-视频)")
    private Integer uptype;

    @ApiModelProperty(value = "帖子类型")
    private Integer cid;

    @ApiModelProperty(value = "帖子类型名称")
    private String cname;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "0-小程序用户，1-后台发布者")
    private Integer adminId;

    @ApiModelProperty(value = "用户姓名")
    private String uname;

    @ApiModelProperty(value = "用户头像")
    private String img;

    @ApiModelProperty(value = "链接")
    private String url;

    @ApiModelProperty(value = "动态图片")
    private String sliderImage;

    @ApiModelProperty(value = "文章图片 前端用")
    private List<String> sliderImages = new ArrayList<>();

    @ApiModelProperty(value = "动态标题")
    private String title;

    @ApiModelProperty(value = "文章简介")
    private String synopsis;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "浏览次数")
    private Integer visit;

    @ApiModelProperty(value = "评论次数")
    private Integer review;

    @ApiModelProperty(value = "分享次数")
    private Integer share;

    @ApiModelProperty(value = "收藏次数")
    private Integer collect;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "是否热门")
    private Boolean isHot;

    @ApiModelProperty(value = "是否置顶")
    private Boolean isTop;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否关注")
    private Boolean isGz;

    @ApiModelProperty(value = "是否点赞")
    private Boolean isDz;

    @ApiModelProperty(value = "是否收藏")
    private Boolean isSc;
}