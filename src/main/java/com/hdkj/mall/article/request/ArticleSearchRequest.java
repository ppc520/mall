package com.hdkj.mall.article.request;

import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 *
 * 文章管理 搜索Request

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ArticleSearchRequest对象", description="文章管理表")
public class ArticleSearchRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "分类id", example = "")
    private String cid;

    @ApiModelProperty(value = "学校id", example = "")
    private Integer pid;

    @ApiModelProperty(value = "发布人id")
    private Integer uid;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "封面图片类型")
    private Integer fileType;

    @ApiModelProperty(value = "显示方式")
    private Integer flag;

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;

    @ApiModelProperty(value = "是否有微信素材媒体id")
    private Boolean isHaveMediaId = null;

    @ApiModelProperty(value = "是否热门(小程序)", example = "")
    private Boolean isHot = null;

    @ApiModelProperty(value = "是否热门置顶")
    private Boolean isTop;

    @ApiModelProperty(value = "是否轮播图(小程序)" , example = "")
    private Boolean isBanner = null;

    @ApiModelProperty(value = "是否隐藏", example = "")
    private Boolean hide = null;

    @ApiModelProperty(value = "状态" , example = "")
    private Boolean status = null;

}
