package com.hdkj.mall.article.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 文章管理 Request

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleRequest对象", description="文章管理表")
public class ArticleRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分类id", required = false)
//    @NotBlank(message = "请选择分类")
    private String cid;

    @ApiModelProperty(value = "学校id", required = false)
//    @NotNull(message = "请选择学校")
    private Integer pid;

    @ApiModelProperty(value = "发布人id", required = false)
//    @NotNull(message = "请选择发布人")
    private Integer uid;

    @ApiModelProperty(value = "类型", required = true)
    @NotNull(message = "请选择类型")
    private Integer type;

    @ApiModelProperty(value = "封面图片类型", required = true)
    @NotNull(message = "请选择封面图片类型")
    private Integer fileType;

    @ApiModelProperty(value = "显示方式", required = true)
    @NotNull(message = "请选择封面显示方式")
    private Integer flag;

    @ApiModelProperty(value = "文章标题", required = true)
    @NotBlank(message = "请填写标题")
    @Length(max = 200, message = "标题最多200个字符")
    private String title;

    @ApiModelProperty(value = "文章作者", required = true)
    @NotBlank(message = "请填写作者")
    @Length(max = 50, message = "文章作者最多50个字符")
    private String author;

    @ApiModelProperty(value = "文章作者头像", required = false)
//    @NotBlank(message = "请上传文章作者头像")
    private String image;

    @ApiModelProperty(value = "封面图片", required = false)
//    @NotBlank(message = "请上传封面图片")
    private String imageInput;

    @ApiModelProperty(value = "文章简介", required = false)
//    @Length(max = 200, message = "文章简介最多200个字符")
//    @NotBlank(message = "请填写文章简介")
    private String synopsis;

    @ApiModelProperty(value = "文章分享标题", required = false)
//    @NotBlank(message = "请填写文章分享标题")
//    @Length(max = 200, message = "文章分享标题最多200个字符")
    private String shareTitle;

    @ApiModelProperty(value = "文章分享简介", required = false)
//    @NotBlank(message = "请填写文章分享简介")
//    @Length(max = 200, message = "文章分享简介最多200个字符")
    private String shareSynopsis;

    @ApiModelProperty(value = "排序", example = "0", required = true)
    private Integer sort;

    @ApiModelProperty(value = "原文链接")
    private String url;

    @ApiModelProperty(value = "是否热门(小程序)", example = "false")
    private Boolean isHot;

    @ApiModelProperty(value = "是否置顶(小程序)", example = "false")
    private Boolean isTop;

    @ApiModelProperty(value = "是否轮播图(小程序)" , example = "true")
    private Boolean isBanner;

    @ApiModelProperty(value = "是否删除" , example = "false")
    private Boolean status;

    @ApiModelProperty(value = "是否显示" , example = "false")
    private Boolean hide;

    @ApiModelProperty(value = "文章内容", required = true)
//    @NotBlank(message = "请填写文章内容")
    private String content;
}
