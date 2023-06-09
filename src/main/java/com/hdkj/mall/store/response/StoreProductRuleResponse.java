package com.hdkj.mall.store.response;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hdkj.mall.store.request.StoreProductRuleItemRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 商品规则值(规格)表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("eb_store_product_rule")
@ApiModel(value="StoreProductRuleResponse对象", description="商品规则值(规格)表")
public class StoreProductRuleResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "id")
    private Integer id;

    @ApiModelProperty(value = "规格名称")
    private String ruleName;

    @ApiModelProperty(value = "规格值【JSON字符串】")
    private List<StoreProductRuleItemRequest> ruleValue;
}
