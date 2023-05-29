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
 * 活动管理表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ActivityVo对象", description="活动管理表")
public class ActivityVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "活动管理ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "小区ID")
    private Integer pid;

    @ApiModelProperty(value = "小区名称")
    private String pname;

    @ApiModelProperty(value = "活动类型id")
    private Integer cid;

    @ApiModelProperty(value = "活动类型名称")
    private String cname;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "用户姓名")
    private String uname;

    @ApiModelProperty(value = "链接")
    private String url;

    @ApiModelProperty(value = "动态图片")
    private String sliderImage;

    @ApiModelProperty(value = "文章图片 前端用")
    private List<String> sliderImages = new ArrayList<>();

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

    @ApiModelProperty(value = "报名是否开始")
    private Boolean isKSBm;

    @ApiModelProperty(value = "报名是否过期")
    private Boolean isBmGQ;

    @ApiModelProperty(value = "活动是否过期")
    private Boolean isHdGQ;

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

    @ApiModelProperty(value = "报名次数")
    private Integer review;

    @ApiModelProperty(value = "评论次数")
    private Integer pl;

    @ApiModelProperty(value = "点赞次数")
    private Integer zan;

    @ApiModelProperty(value = "是否点赞")
    private Boolean isZan;

    @ApiModelProperty(value = "是否关注")
    private Boolean isGz;

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

    @ApiModelProperty(value = "是否报名")
    private Boolean isBm;

    @ApiModelProperty(value = "是否收藏")
    private Boolean isSc;

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


}
