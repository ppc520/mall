package com.hdkj.mall.article.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 动态点赞管理表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DynamicPraise对象", description="动态点赞管理表")
public class DynamicPraiseVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "动态评论管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "类型（0-动态点赞，1-分享，2-收藏，3-评论点赞）")
    private Integer type;

    @ApiModelProperty(value = "动态ID")
    private Integer did;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "用户昵称")
    private String name;

    @ApiModelProperty(value = "用户头像")
    private String img;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否查看(0-否，1-是)")
    private Boolean flag;

    private DynamicVo dynamic;

}
