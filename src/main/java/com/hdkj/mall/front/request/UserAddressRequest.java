package com.hdkj.mall.front.request;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户地址表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="UserAddressRequest对象", description="用户地址")
public class UserAddressRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户地址id")
    @TableId(value = "id")
    private Integer id;

    @ApiModelProperty(value = "收货人姓名", required = true)
    @NotBlank
    private String realName;

    @ApiModelProperty(value = "收货人电话", required = true)
    @NotBlank
    private String phone;

    @ApiModelProperty(value = "收货人详细地址", required = true)
    @NotBlank
    private String detail;

    @ApiModelProperty(value = "是否默认", example = "false", required = true)
    private Boolean isDefault;

    @Valid
    @ApiModelProperty(value = "城市信息", required = true)
    private UserAddressCityRequest address;
}
