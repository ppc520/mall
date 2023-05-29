package com.constants;

/**
 * 积分记录常量类
 */
public class IntegralRecordConstants {

    /** 佣金记录类型—增加 */
    public static final Integer INTEGRAL_RECORD_TYPE_ADD = 1;

    /** 佣金记录类型—扣减 */
    public static final Integer INTEGRAL_RECORD_TYPE_SUB = 2;

    /** 佣金记录状态—创建 */
    public static final Integer INTEGRAL_RECORD_STATUS_CREATE = 1;

    /** 佣金记录状态—冻结期 */
    public static final Integer INTEGRAL_RECORD_STATUS_FROZEN = 2;

    /** 佣金记录状态—完成 */
    public static final Integer INTEGRAL_RECORD_STATUS_COMPLETE = 3;

    /** 佣金记录状态—失效（订单退款） */
    public static final Integer INTEGRAL_RECORD_STATUS_INVALIDATION = 4;

    /** 佣金记录关联类型—订单 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_ORDER = "order";

    /** 佣金记录关联类型—签到 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SIGN = "sign";

    /** 佣金记录关联类型—系统后台 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_SYSTEM = "system";

    /** 佣金记录标题—用户订单付款成功 */
    public static final String BROKERAGE_RECORD_TITLE_ORDER = "用户订单付款成功";

    /** 佣金记录标题—签到经验奖励 */
    public static final String BROKERAGE_RECORD_TITLE_SIGN = "签到积分奖励";

    /** 佣金记录标题—后台积分操作 */
    public static final String BROKERAGE_RECORD_TITLE_SYSTEM = "后台积分操作";

    /** 佣金记录标题—订单退款 */
    public static final String BROKERAGE_RECORD_TITLE_REFUND = "订单退款";


    /** 佣金记录标题—用户预约洗衣 */
    public static final String BROKERAGE_RECORD_TITLE_XY = "用户预约洗衣";

    /** 佣金记录标题—用户预约回收 */
    public static final String BROKERAGE_RECORD_TITLE_HS = "用户预约回收";

    /** 佣金记录标题—用户预约维修*/
    public static final String BROKERAGE_RECORD_TITLE_WX = "用户预约维修";


    /** 佣金记录关联类型—用户预约洗衣 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_XY= "xy";
    /** 佣金记录关联类型—用户预约回收 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_HS= "hs";
    /** 佣金记录关联类型—用户预约维修 */
    public static final String INTEGRAL_RECORD_LINK_TYPE_WX= "wx";


    /** 佣金记录标题—用户充值 */
    public static final String BROKERAGE_RECORD_TITLE_RECHARGE = "用户充值";

    /** 佣金记录标题—用户提现 */
    public static final String BROKERAGE_RECORD_TITLE_TRANSFER = "用户提现";


}
