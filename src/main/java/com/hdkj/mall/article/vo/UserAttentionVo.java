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
 * 用户关注表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserAttentionVo对象", description="用户关注表")
public class UserAttentionVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户关注记录ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关注的用户id")
    private Integer did;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "0-小程序用户，1-后台发布人")
    private Integer type;

    @ApiModelProperty(value = "关注的用户昵称")
    private String name;

    @ApiModelProperty(value = "关注的用户头像")
    private String img;

    @ApiModelProperty(value = "关注的时间")
    private Date createTime;

    @ApiModelProperty(value = "是否关注")
    private Boolean isGz;

}
