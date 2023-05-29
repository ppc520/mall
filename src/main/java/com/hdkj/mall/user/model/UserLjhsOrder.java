package com.hdkj.mall.user.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户垃圾回收订单表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_ljhs_order")
@ApiModel(value="UserLjhsOrder对象", description="用户垃圾回收订单表")
public class UserLjhsOrder implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

    @ApiModelProperty(value = "订单类型（0：洗衣；1：垃圾回收，2：维修）")
    private Integer orderType;

    @ApiModelProperty(value = "代仍类型时（1-帮我送，2-帮我取，3-帮我买，4-帮我丢）")
    private Integer opType;

    @ApiModelProperty(value = "用户uid")
    private Integer uid;

    @ApiModelProperty(value = "联系人姓名")
    private String userName;

    @ApiModelProperty(value = "联系人电话")
    private String userPhone;

    @ApiModelProperty(value = "联系人地址")
    private String userAddress;

    @ApiModelProperty(value = "联系人地址，代仍类型时（1-帮我送，2-帮我取） 不为空")
    private String userAddress1;

    @ApiModelProperty(value = "预约时间")
    private String yytime;

    @ApiModelProperty(value = "垃圾类型ID")
    private Integer ljlxId;

    @ApiModelProperty(value = "联系人所在小区")
    private Integer pid;

    @ApiModelProperty(value = "大概重量或体积")
    private String zltj;

    @ApiModelProperty(value = "实际重量或体积")
    private String sjzltj;

    @ApiModelProperty(value = "用户备注")
    private String mark;

    @ApiModelProperty(value = "详情图片")
    private String detailImages;

    @ApiModelProperty(value = "接单人详情图片")
    private String detailImages1;

    @ApiModelProperty(value = "时效")
    private String sx;

    @ApiModelProperty(value = "获得碳币")
    private Integer tanbi;

    @ApiModelProperty(value = "获得金币")
    private String jinbi;

    @ApiModelProperty(value = "获得积分")
    private Integer jifen;

    @ApiModelProperty(value = "支付金额")
    private String price;

    @ApiModelProperty(value = "实际支付金额")
    private String payPrice;

    @ApiModelProperty(value = "支付方式（0：线上支付；1：线下微信/现金支付）")
    private Integer payType;

    @ApiModelProperty(value = "支付方式（0：余额支付；1：微信支付）")
    private Integer payType1;

    @ApiModelProperty(value = "0 未退款 1 申请中 2 已退款 3 退款中")
    private Integer refundStatus;

    @ApiModelProperty(value = "是否已支付（0：未支付；1：已支付）")
    private Integer paid;

//    洗衣
//    0-未接单，1-已接单，2-待处理，3-已确认，4-商家待确认，5-商家已确认；51-商家退回，6-商家已发货，7-用户确认收货，8-订单已完成，
//    0-未接单，9-取消
//    0-未接单，1-已接单，9-取消

//    维修
//    0-未接单，1-已接单，2-待处理，3-已确认，4-商家待确认，41-商家退回，5-商家已确认；51-用户已付款，6-商家已发货，7-用户确认收货，8-订单已完成，
//    0-未接单，9-取消
//    0-未接单，1-已接单，9-取消

    @ApiModelProperty(value = "订单状态")
    private Integer status;

    @ApiModelProperty(value = "接单用户id")
    private Integer cuid;

    @ApiModelProperty(value = "接单人姓名")
    private String cname;

    @ApiModelProperty(value = "接单人电话")
    private String cphone;

    @ApiModelProperty(value = "接单用户备注")
    private String remark;

    @ApiModelProperty(value = "接单时间")
    private Date ctime;

    @ApiModelProperty(value = "完成时间")
    private Date wtime;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "是否删除")
    private Integer isDel;

    @ApiModelProperty(value = "扩展字段")
    private String kzzd;

    @ApiModelProperty(value = "扩展字段")
    private String uniname;

    @ApiModelProperty(value = "洗衣-维修：订单码")
    private String orderCode;

    @ApiModelProperty(value = "退回原因")
    private String cause;

    @ApiModelProperty(value = "退回原因图片")
    private String causeImage;

}
