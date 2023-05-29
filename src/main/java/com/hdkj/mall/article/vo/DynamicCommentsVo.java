package com.hdkj.mall.article.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hdkj.mall.article.model.DynamicComments;
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
 * 动态评论管理表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="DynamicCommentsVo对象", description="动态评论管理表")
public class DynamicCommentsVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "动态评论管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "父ID")
    private Integer pid;

    @ApiModelProperty(value = "动态ID")
    private Integer did;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "是否点赞")
    private Boolean isDz;

    @ApiModelProperty(value = "点赞次数")
    private Integer dzcs;

    @ApiModelProperty(value = "评论次数")
    private Integer plcs;

    @ApiModelProperty(value = "用户昵称")
    private String name;

    @ApiModelProperty(value = "用户头像")
    private String img;

    @ApiModelProperty(value = "动态评论")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "是否查看(0-否，1-是)")
    private Boolean flag;

    @JsonInclude(JsonInclude.Include.NON_EMPTY) //属性为 空（""）[] 或者为 NULL 都不序列化
    private List<DynamicComments> child = new ArrayList<>();

    private DynamicVo dynamic;
}
