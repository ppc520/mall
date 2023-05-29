package com.hdkj.mall.user.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户垃圾回收订单表
 */
@Data
@ApiModel(value="UserOthersOrderResponse对象", description="其他服务订单表")
public class UserOthersOrderResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "'订单类型（1：银行；2：运营商）',")
    private Integer orderType;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "申请人姓名")
    private String userName;

    @ApiModelProperty(value = "联系人姓名")
    private String uname;

    @ApiModelProperty(value = "联系人电话")
    private String phone;

    @ApiModelProperty(value = "详情图片")
    private String detailImages;

    @ApiModelProperty(value = "订单状态（0：待接单；1：已接单；2：已完成；3:已取消）")
    private Integer status;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String remark;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date update_time;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "扩展字段")
    private String kzzd;
}
