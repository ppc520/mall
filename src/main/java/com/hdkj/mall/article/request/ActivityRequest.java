package com.hdkj.mall.article.request;

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
 * 活动管理表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ActivityRequest对象", description="活动管理表")
public class ActivityRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "活动管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "小区ID")
    private Integer pid;

    @ApiModelProperty(value = "活动类型id")
    private Integer cid;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "封面图片类型")
    private Integer fileType;

    @ApiModelProperty(value = "显示方式")
    private Integer flag;

    @ApiModelProperty(value = "文章作者")
    private String author;

    @ApiModelProperty(value = "文章作者头像")
    private String image;

    @ApiModelProperty(value = "链接")
    private String url;

    @ApiModelProperty(value = "动态图片")
    private String sliderImage;

    @ApiModelProperty(value = "活动标题")
    private String title;

    @ApiModelProperty(value = "活动内容")
    private String content;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "电话")
    private String phone;

    @ApiModelProperty(value = "是否收费(0-否，1-是)")
    private Boolean isPrice;

    @ApiModelProperty(value = "费用（是否收费为1时，费用price不为空）")
    private String price;

    @ApiModelProperty(value = "报名开始时间")
    private Date signStart;

    @ApiModelProperty(value = "报名结束时间")
    private Date signEnd;

    @ApiModelProperty(value = "活动开始时间")
    private Date activityStart;

    @ApiModelProperty(value = "活动结束时间")
    private Date activityEnd;

    @ApiModelProperty(value = "是否审核(0-未审核，1-审核通过，2-审核不通过)")
    private Integer isCheck;

    @ApiModelProperty(value = "审核不通过原因")
    private String cause;

    @ApiModelProperty(value = "浏览次数")
    private Integer visit;

    @ApiModelProperty(value = "评论次数")
    private Integer review;

    @ApiModelProperty(value = "点赞次数")
    private Integer zan;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "管理员id")
    private Integer adminId;

    @ApiModelProperty(value = "是否热门")
    private Boolean isHot;

    @ApiModelProperty(value = "是否置顶")
    private Boolean isTop;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "创建时间")
    private Date updateTime;

    @ApiModelProperty(value = "搜索关键字")
    private String keywords;
}
