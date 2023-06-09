package com.hdkj.mall.store.request;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 商品描述查询对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_description")
@ApiModel(value="StoreProductDescription对象", description="")
public class StoreProductDescriptionSearchRequest {
    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品详情")
    private String description;

    @ApiModelProperty(value = "商品类型")
    private Boolean type;

}
