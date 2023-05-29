package com.hdkj.mall.front.response;

import com.hdkj.mall.combination.response.StorePinkResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * 拼团商品响应体
 */
@Data
public class CombinationDetailResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "拼团列表")
    private List<StorePinkResponse> pinkList;

    @ApiModelProperty(value = "拼团成功列表")
    private List<StorePinkResponse> pinkOkList;

    @ApiModelProperty(value = "拼团完成的商品总件数")
    private Integer pinkOkSum;

    @ApiModelProperty(value = "拼团商品信息")
    private CombinationDetailH5Response storeCombination;

    @ApiModelProperty(value = "商品规格")
    private List<ProductAttrResponse> productAttr;

    @ApiModelProperty(value = "商品规格值")
    private HashMap<String,Object> productValue;

    @ApiModelProperty(value = "收藏标识")
    private Boolean userCollect;
}
