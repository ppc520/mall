package com.hdkj.mall.wechat.response;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 微信用户授权返回数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="WeChatAuthorizeLoginGetOpenIdResponse对象", description="微信用户授权返回数据")
public class WeChatAuthorizeLoginGetOpenIdResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "token")
    @TableField(value = "access_token")
    private String accessToken;

    @ApiModelProperty(value = "过期时间")
    @TableField(value = "expires_in")
    private String expiresIn;

    @ApiModelProperty(value = "token")
    @TableField(value = "refresh_token")
    private String refreshToken;

    @ApiModelProperty(value = "用户OpenId")
    @TableField(value = "openid")
    private String openId;

    @ApiModelProperty(value = "类型")
    private String scope;

}
