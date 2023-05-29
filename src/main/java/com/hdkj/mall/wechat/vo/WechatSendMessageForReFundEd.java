package com.hdkj.mall.wechat.vo;

import lombok.Data;

/**
 * 退款通知
 */
@Data
public class WechatSendMessageForReFundEd {
    public WechatSendMessageForReFundEd(String tuiKuanZhuangTai, String tuiKuanShangPin, String tuiKuanJinE, String chuLiShiJian, String tuiKuaShuoMing, String tuiKuaDanHao, String dingDanBianHao, String xiaDanShiJian, String zhiFuJinE, String shangPinMingChneg, String tuiKuanYuanYin, String menDianMingCheng, String beiZhu, String sheBeiMingCheng) {
        TuiKuanZhuangTai = tuiKuanZhuangTai;
        TuiKuanShangPin = tuiKuanShangPin;
        TuiKuanJinE = tuiKuanJinE;
        ChuLiShiJian = chuLiShiJian;
        TuiKuanShuoMing = tuiKuaShuoMing;
        TuiKuaDanHao = tuiKuaDanHao;
        DingDanBianHao = dingDanBianHao;
        XiaDanShiJian = xiaDanShiJian;
        ZhiFuJinE = zhiFuJinE;
        ShangPinMingChneg = shangPinMingChneg;
        TuiKuanYuanYin = tuiKuanYuanYin;
        MenDianMingCheng = menDianMingCheng;
        BeiZhu = beiZhu;
        SheBeiMingCheng = sheBeiMingCheng;
    }

    private String TuiKuanZhuangTai;
    private String TuiKuanShangPin;
    private String TuiKuanJinE;
    private String ChuLiShiJian;
    private String TuiKuanShuoMing;
    private String TuiKuaDanHao;
    private String DingDanBianHao;
    private String XiaDanShiJian;
    private String ZhiFuJinE;
    private String ShangPinMingChneg;
    private String TuiKuanYuanYin;
    private String MenDianMingCheng;
    private String BeiZhu;
    private String SheBeiMingCheng;
}
