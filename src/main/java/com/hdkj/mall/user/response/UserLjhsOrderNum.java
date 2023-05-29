package com.hdkj.mall.user.response;

import lombok.Data;

/**
 * 用户垃圾代仍订单数量
 */
@Data
public class UserLjhsOrderNum {

    // 总数量
    private Integer allOrderNum;
    // 待接单数量
    private Integer noOrderNum;
    // 已接单数量
    private Integer haveOrderNum;
    // 已完成数量
    private Integer yesOrderNum;
    // 已取消数量
    private Integer cancelOrderNum;
    // 已删除数量
    private Integer delOrderNum;
    // 服务中数量
    private Integer serOrderNum;
    // 待评价订单数量
    private Integer noPjOrderNum;
}
