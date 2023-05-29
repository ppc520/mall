package com.hdkj.mall.store.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="StoreCartResponse", description="购物车ListResponse")
public class StoreCartResponse implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(value = "购物车表ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Integer uid;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "商品ID")
    private Integer productId;

    @ApiModelProperty(value = "商品属性")
    private String productAttrUnique;

    @ApiModelProperty(value = "商品数量")
    private Integer cartNum;

    @ApiModelProperty(value = "是否为立即购买")
    private Boolean isNew;

    @ApiModelProperty(value = "拼团id")
    private Integer combinationId;

    @ApiModelProperty(value = "秒杀商品ID")
    private Integer seckillId;

    @ApiModelProperty(value = "砍价id")
    private Integer bargainId;

    /**
     * 产品详情
     */
    @ApiModelProperty(value = "产品详情")
    private StoreProductCartProductInfoResponse productInfo;

    // 手动添加
    @ApiModelProperty(value = "一级分佣")
    private BigDecimal brokerage;

    @ApiModelProperty(value = "二级分佣")
    private BigDecimal brokerageTwo;

    @ApiModelProperty(value = "商品是否有效")
    private Boolean attrStatus;

    // todo 价格计算有问题
    // 真实价格
    private BigDecimal truePrice;
    // 会员价格
    private BigDecimal vipTruePrice;
    // 真实库存
    private Integer trueStock;
    // 原价
    private BigDecimal costPrice;

    private Integer isReply;
    private String addTime;

    @ApiModelProperty(value = "团长拼团id")
    private Integer pinkId;
}
