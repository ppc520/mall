package com.hdkj.mall.front.response;

import com.hdkj.mall.bargain.model.StoreBargain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 砍价首页响应对象
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="BargainIndexResponse对象", description="砍价首页响应对象")
public class BargainIndexResponse {

    @ApiModelProperty(value = "拼团商品列表")
    private List<StoreBargain> productList;

}
