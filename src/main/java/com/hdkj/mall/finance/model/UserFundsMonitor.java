package com.hdkj.mall.finance.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户充值对象类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserFundsMonitor对象", description="佣金")
public class UserFundsMonitor implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "充值用户UID")
    private Integer uid;

    @ApiModelProperty(value = "昵称")
    private String nickname;

    @ApiModelProperty(value = "账户余额")
    private BigDecimal nowMoney;

    @ApiModelProperty(value = "账户佣金")
    private BigDecimal brokerage;

    @ApiModelProperty(value = "账户总佣金")
    private BigDecimal totalBrokerage;

    @ApiModelProperty(value = "提现总金额")
    private BigDecimal totalExtract;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "推广员UID")
    private Integer spreadUid;

    @ApiModelProperty(value = "推广员昵称")
    private String spreadName;
}
