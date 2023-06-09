package com.hdkj.mall.wechat.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * WechatProgramPublicTemp对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_wechat_program_public_temp")
@ApiModel(value="WechatProgramPublicTemp对象", description="")
public class WechatProgramPublicTempRequest implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "微信模板id")
    private Integer tid;

    @ApiModelProperty(value = "模版标题")
    private String title;

    @ApiModelProperty(value = "模版类型，2 为一次性订阅，3 为长期订阅")
    private Integer type;

    @ApiModelProperty(value = "模版所属类目 id")
    private Integer categoryId;


}
