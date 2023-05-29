package com.hdkj.mall.front.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 文章响应对象

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleResponse对象", description="文章响应对象")
public class ArticleResponse implements Serializable {

    private static final long serialVersionUID = -4585094537501770138L;

    @ApiModelProperty(value = "文章管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "文章标题")
    private String title;

    @ApiModelProperty(value = "发布人id")
    private Integer uid;

    @ApiModelProperty(value = "文章作者")
    private String author;

    @ApiModelProperty(value = "显示方式")
    private Integer flag;

    @ApiModelProperty(value = "文章作者头像")
    private String image;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "封面图片类型")
    private Integer fileType;

    @ApiModelProperty(value = "文章图片 前端用")
    private String imageInput;

    @ApiModelProperty(value = "文章图片 前端用")
    private List<String> imageInputs;

    @ApiModelProperty(value = "文章简介")
    private String synopsis;

    @ApiModelProperty(value = "浏览次数")
    private String visit;

    @ApiModelProperty(value = "点赞次数")
    private Integer zan;

    @ApiModelProperty(value = "点赞次数")
    private Integer pl;

    @ApiModelProperty(value = "是否点赞")
    private Boolean isZan;

    @ApiModelProperty(value = "是否关注")
    private Boolean isGz;

    @ApiModelProperty(value = "文章内容")
    private String content;

    @ApiModelProperty(value = "置顶")
    private Boolean isTop;

    @ApiModelProperty(value = "是否热门(小程序)")
    private Boolean isHot;

    @ApiModelProperty(value = "原文链接")
    private String url;

    @ApiModelProperty(value = "创建时间")
//    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
//    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private Date updateTime;
}
