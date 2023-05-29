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
 * 用户垃圾回收订单状态表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_ljhs_order_status")
@ApiModel(value="UserLjhsOrderStatus对象", description="服务订单状态表")
public class UserLjhsOrderStatus implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    private String orderId;

//    0-未接单，1-已接单，2-待处理，3-已确认，4-商家待确认，5-商家已确认；51-商家退回，6-商家已发货，7-用户确认收货，8-订单已完成，
//    0-未接单，9-取消
//    0-未接单，1-已接单，9-取消
    @ApiModelProperty(value = "订单状态")
    private Integer status;

    @ApiModelProperty(value = "添加时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date update_time;

}
