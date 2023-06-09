package com.hdkj.mall.finance.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 充值退款请求对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserRechargeRefundRequest对象", description="")
public class UserRechargeRefundRequest {

    @ApiModelProperty(value = "用户充值订单id")
    @NotNull(message = "用户充值订单编号不能为空")
    private Integer id;

    @ApiModelProperty(value = "退款类型：1-仅本金，2-本金+赠送")
    @NotNull(message = "退款类型不能为空")
    @Range(min = 1, max = 2, message = "未知的退款类型")
    private Integer type;

}
