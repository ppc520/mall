package com.hdkj.mall.front.vo;

import com.hdkj.mall.store.model.StoreOrder;
import com.hdkj.mall.store.vo.StoreOrderInfoOldVo;
import lombok.Data;

import java.util.List;

/**
 * 再次下单VO对象
 */
@Data
public class OrderAgainVo {
    private StoreOrder storeOrder;
    private List<StoreOrderInfoOldVo> cartInfo;
    private OrderAgainItemVo status;
    private String payTime;
    private String addTime;
    private String statusPic;
    private Integer offlinePayStatus;
}
