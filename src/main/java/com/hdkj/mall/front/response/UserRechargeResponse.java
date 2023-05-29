package com.hdkj.mall.front.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 充值响应对象

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserRechargeResponse对象", description="充值响应对象")
public class UserRechargeResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "充值套餐列表")
    private List<UserRechargeItemResponse> rechargeQuota;

    @ApiModelProperty(value = "注意事项")
    private List<String> rechargeAttention;
}
