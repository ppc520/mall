package com.hdkj.mall.front.response;

import com.hdkj.mall.category.model.Category;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 用户地址表

 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="IndexInfoItemResponse对象", description="用户登录返回数据")
public class IndexInfoItemResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "首页促销单品")
    private String promotionNumber;

    @ApiModelProperty(value = "分类")
    private List<Category> fastList;

    @ApiModelProperty(value = "精品推荐")
    private List<ProductResponse> bastList;

    @ApiModelProperty(value = "首发新品")
    private List<ProductResponse> firstList;

    @ApiModelProperty(value = "首页精品推荐图片")
    private List<HashMap<String, Object>> bastBanner;

}
