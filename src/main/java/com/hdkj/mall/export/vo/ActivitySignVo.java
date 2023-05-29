package com.hdkj.mall.export.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 活动报名ExcelVo对象类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="ActivitySignVo对象", description = "活动报名导出")
public class ActivitySignVo implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "用户名称")
    private String name;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "报名时间")
    private String createTime;

    @ApiModelProperty(value = "备注")
    private String content;

}
