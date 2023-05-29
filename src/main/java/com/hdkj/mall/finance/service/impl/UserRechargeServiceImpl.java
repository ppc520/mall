package com.hdkj.mall.finance.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.CommonPage;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.WeChatConstants;
import com.exception.MallException;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.payment.vo.wechat.WxRefundResponseVo;
import com.hdkj.mall.finance.dao.UserRechargeDao;
import com.hdkj.mall.finance.model.UserRecharge;
import com.hdkj.mall.finance.request.UserRechargeSearchRequest;
import com.hdkj.mall.finance.response.UserRechargeResponse;
import com.hdkj.mall.finance.service.UserRechargeService;
import com.hdkj.mall.payment.vo.wechat.WxRefundVo;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.user.model.User;
import com.hdkj.mall.user.model.UserBill;
import com.hdkj.mall.user.service.UserBillService;
import com.utils.MallUtil;
import com.utils.DateUtil;
import com.utils.RestTemplateUtil;
import com.utils.XmlUtil;
import com.utils.vo.dateLimitUtilVo;
import com.hdkj.mall.finance.request.UserRechargeRefundRequest;
import com.hdkj.mall.front.request.UserRechargeRequest;
import com.hdkj.mall.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
* UserRechargeServiceImpl 接口实现
*/
@Service
public class UserRechargeServiceImpl extends ServiceImpl<UserRechargeDao, UserRecharge> implements UserRechargeService {

    @Resource
    private UserRechargeDao dao;

    @Autowired
    private UserService userService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RestTemplateUtil restTemplateUtil;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private UserBillService userBillService;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhou
    * @since 2022-05-11
    * @return List<UserRecharge>
    */
    @Override
    public PageInfo<UserRechargeResponse> getList(UserRechargeSearchRequest request, PageParamRequest pageParamRequest) {
        Page<UserRecharge> userRechargesList = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        dateLimitUtilVo dateLimit = DateUtil.getDateLimit(request.getDateLimit());
        //带 UserExtract 类的多条件查询
        LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotNull(request.getUid()) && request.getUid() > 0) {
            lambdaQueryWrapper.eq(UserRecharge::getUid, request.getUid());
        }
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.and(i -> i.
                    or().like(UserRecharge::getOrderId, request.getKeywords()) //订单号
            );
        }
        //是否充值
        if(request.getPaid() != null){
            lambdaQueryWrapper.eq(UserRecharge::getPaid, request.getPaid());
        }

        //时间范围
        if(dateLimit.getStartTime() != null && dateLimit.getEndTime() != null){
            //判断时间
            int compareDateResult = DateUtil.compareDate(dateLimit.getEndTime(), dateLimit.getStartTime(), Constants.DATE_FORMAT);
            if(compareDateResult == -1){
                throw new MallException("开始时间不能大于结束时间！");
            }

            lambdaQueryWrapper.between(UserRecharge::getCreateTime, dateLimit.getStartTime(), dateLimit.getEndTime());
        }
        lambdaQueryWrapper.orderByDesc(UserRecharge::getId);
        List<UserRechargeResponse> responses = new ArrayList<>();
        List<UserRecharge> userRecharges = dao.selectList(lambdaQueryWrapper);
        List<Integer> userIds = userRecharges.stream().map(UserRecharge::getUid).collect(Collectors.toList());
        HashMap<Integer, User> userHashMap = new HashMap<>();

        if(userIds.size() > 0)
        userHashMap = userService.getMapListInUid(userIds);

        HashMap<Integer, User> finalUserHashMap = userHashMap;
        userRecharges.stream().map(e-> {
            User user = finalUserHashMap.get(e.getUid());
            UserRechargeResponse r = new UserRechargeResponse();
            BeanUtils.copyProperties(e,r);
            if(null != user){
                r.setAvatar(user.getAvatar());
                r.setNickname(user.getNickname());
            }
            responses.add(r);
            return e;
        }).collect(Collectors.toList());
        return CommonPage.copyPageInfo(userRechargesList,responses);
    }

    /**
     * 充值总金额
     * @author Mr.Zhou
     * @since 2022-05-11
     * @return HashMap<String, BigDecimal>
     */
    @Override
    public HashMap<String, BigDecimal> getBalanceList() {
        HashMap<String, BigDecimal> map = new HashMap<>();

        BigDecimal routine = dao.getSumByType("routine");
        if(null == routine) routine = BigDecimal.ZERO;
        map.put("routine", routine); //小程序充值

//        BigDecimal weChat = dao.getSumByType("weixin");
        BigDecimal weChat = dao.getSumByType("public");
        if(null == weChat) weChat = BigDecimal.ZERO;
        map.put("weChat", weChat); //公众号充值

        BigDecimal total = dao.getSumByType("");
        if(null == total) total = BigDecimal.ZERO;
        map.put("total", total); //总金额

        BigDecimal refund = dao.getSumByRefund();
        if(null == refund) refund = BigDecimal.ZERO;
        map.put("refund", refund);

        map.put("other", total.subtract(routine).subtract(weChat)); //其他金额

        return map;
    }

    /**
     * 根据对象查询订单
     * @author Mr.Zhou
     * @since 2022-05-11
     * @return UserRecharge
     */
    @Override
    public UserRecharge getInfoByEntity(UserRecharge userRecharge) {
        LambdaQueryWrapper<UserRecharge> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.setEntity(userRecharge);
        return dao.selectOne(lambdaQueryWrapper);
    }

    /**
     * 创建订单对象
     * @author Mr.Zhou
     * @since 2022-05-11
     * @return UserRecharge
     */
    @Override
    public UserRecharge create(UserRechargeRequest request) {
        UserRecharge userRecharge = new UserRecharge();
        userRecharge.setUid(request.getUserId());
        userRecharge.setOrderId(MallUtil.getOrderNo(Constants.PAY_TYPE_WE_CHAT));
        userRecharge.setPrice(request.getPrice());
        userRecharge.setGivePrice(request.getGivePrice());
        userRecharge.setRechargeType(request.getFromType());
        save(userRecharge);
        return userRecharge;
    }

    /**
     * 更新充值订单
     * @param userRecharge UserRecharge 充值订单数据
     * @author Mr.Zhou
     * @since 2022-07-01
     */
    @Override
    public Boolean complete(UserRecharge userRecharge) {
        try{
            userRecharge.setPaid(true);
            userRecharge.setPayTime(DateUtil.nowDateTime());
            return updateById(userRecharge);
        }catch (Exception e){
            throw new MallException("回调失败！, 更新状态失败！");
        }
    }

    /**
     * 充值退款
     * @param request 退款参数
     */
    @Override
    public Boolean refund(UserRechargeRefundRequest request) {
        UserRecharge userRecharge = getById(request.getId());
        if (ObjectUtil.isNull(userRecharge)) throw new MallException("数据不存在！");
        if (!userRecharge.getPaid()) throw new MallException("订单未支付");
        if (userRecharge.getPrice().compareTo(userRecharge.getRefundPrice()) == 0) {
            throw new MallException("已退完支付金额!不能再退款了");
        }
        if (userRecharge.getRechargeType().equals("balance")) {
            throw new MallException("佣金转入余额，不能退款");
        }

        User user = userService.getById(userRecharge.getUid());
        if (ObjectUtil.isNull(user)) throw new MallException("用户不存在！");

        // 退款金额
        BigDecimal refundPrice;
        if (request.getType().equals(2)) {// 本金+赠送
            refundPrice = userRecharge.getPrice().add(userRecharge.getGivePrice());
        } else {
            refundPrice = userRecharge.getPrice();
        }

        // 判断充值方式进行退款
        try {
            if (userRecharge.getRechargeType().equals(Constants.PAY_TYPE_WE_CHAT_FROM_PUBLIC)) {// 公众号
                refundJSAPI(userRecharge);
            } else {// 小程序
                refundMiniWx(userRecharge);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new MallException("微信申请退款失败！");
        }

        userRecharge.setRefundPrice(userRecharge.getPrice());

        user.setNowMoney(user.getNowMoney().subtract(refundPrice));

        UserBill userBill = getRefundBill(userRecharge, user.getNowMoney(), refundPrice);

        Boolean execute = transactionTemplate.execute(e -> {
            updateById(userRecharge);
            userService.operationNowMoney(user.getUid(), refundPrice, user.getNowMoney(), "sub");
//            userService.updateById(user);
            userBillService.save(userBill);
            return Boolean.TRUE;
        });

        if (!execute) throw new MallException("充值退款-修改提现数据失败！");
        // 成功后发送小程序通知
        return execute;
    }

    /**
     * 获取用户累计充值金额
     * @param uid 用户uid
     * @return BigDecimal
     */
    @Override
    public BigDecimal getTotalRechargePrice(Integer uid) {
        QueryWrapper<UserRecharge> query = Wrappers.query();
        query.select("IFNULL(SUM(price), 0) as price, IFNULL(SUM(give_price), 0) as give_price");
        query.eq("paid", 1);
        query.eq("uid", uid);
        UserRecharge userRecharge = dao.selectOne(query);
        return userRecharge.getPrice().add(userRecharge.getGivePrice());
    }

    private UserBill getRefundBill(UserRecharge userRecharge, BigDecimal nowMoney, BigDecimal refundPrice) {
        UserBill userBill = new UserBill();
        userBill.setUid(userRecharge.getUid());
        userBill.setLinkId(userRecharge.getOrderId());
        userBill.setPm(0);
        userBill.setTitle("系统充值退款");
        userBill.setCategory(Constants.USER_BILL_CATEGORY_MONEY);
        userBill.setType(Constants.USER_BILL_TYPE_USER_RECHARGE_REFUND);
        userBill.setNumber(refundPrice);
        userBill.setBalance(nowMoney.subtract(refundPrice));
        userBill.setMark(StrUtil.format("退款给用户{}元", userRecharge.getPrice()));
        userBill.setStatus(1);
        userBill.setCreateTime(DateUtil.nowDateTime());
        return userBill;
    }

    /**
     * 小程序退款
     */
    private WxRefundResponseVo refundMiniWx(UserRecharge userRecharge) {
        WxRefundVo wxRefundVo = new WxRefundVo();

        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_ROUTINE_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_MCH_ID);
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(DigestUtils.md5Hex(MallUtil.getUuid() + MallUtil.randomCount(111111, 666666)));
        wxRefundVo.setOut_trade_no(userRecharge.getOrderId());
        wxRefundVo.setOut_refund_no(userRecharge.getOrderId());
        wxRefundVo.setTotal_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        String signKey = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_ROUTINE_APP_KEY);
        String sign = MallUtil.getSign(MallUtil.objectToMap(wxRefundVo), signKey);
        wxRefundVo.setSign(sign);
        String path = systemConfigService.getValueByKeyException("pay_mini_client_p12");
        return commonRefound(wxRefundVo, path);

    }

    /**
     * 公众号退款
     */
    private WxRefundResponseVo refundJSAPI(UserRecharge userRecharge) {
        WxRefundVo wxRefundVo = new WxRefundVo();

        String appId = systemConfigService.getValueByKeyException(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_ID);
        String mchId = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_MCH_ID);
        wxRefundVo.setAppid(appId);
        wxRefundVo.setMch_id(mchId);
        wxRefundVo.setNonce_str(DigestUtils.md5Hex(MallUtil.getUuid() + MallUtil.randomCount(111111, 666666)));
        wxRefundVo.setOut_trade_no(userRecharge.getOrderId());
        wxRefundVo.setOut_refund_no(userRecharge.getOrderId());
        wxRefundVo.setTotal_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        wxRefundVo.setRefund_fee(userRecharge.getPrice().multiply(BigDecimal.TEN).multiply(BigDecimal.TEN).intValue());
        String signKey = systemConfigService.getValueByKey(Constants.CONFIG_KEY_PAY_WE_CHAT_APP_KEY);
        String sign = MallUtil.getSign(MallUtil.objectToMap(wxRefundVo), signKey);
        wxRefundVo.setSign(sign);
        String path = systemConfigService.getValueByKeyException("pay_routine_client_p12");
        return commonRefound(wxRefundVo, path);
    }

    /**
     * 公共退款部分
     */
    private WxRefundResponseVo commonRefound(WxRefundVo wxRefundVo, String path) {
        String xmlStr = XmlUtil.objectToXml(wxRefundVo);
        String url = WeChatConstants.PAY_API_URL + WeChatConstants.PAY_REFUND_API_URI_WECHAT;
        HashMap<String, Object> map = CollUtil.newHashMap();
        String xml = "";
        System.out.println("微信申请退款xmlStr = " + xmlStr);
        try {
            xml = restTemplateUtil.postWXRefundXml(url, xmlStr, wxRefundVo.getMch_id(), path);
            map = XmlUtil.xmlToMap(xml);
        } catch (Exception e) {
            e.printStackTrace();
            throw new MallException("xmlToMap错误，xml = " + xml);
        }
        if(null == map){
            throw new MallException("微信退款失败！");
        }

        WxRefundResponseVo responseVo = MallUtil.mapToObj(map, WxRefundResponseVo.class);
        if(responseVo.getReturnCode().toUpperCase().equals("FAIL")){
            throw new MallException("微信退款失败1！" +  responseVo.getReturnMsg());
        }

        if(responseVo.getResultCode().toUpperCase().equals("FAIL")){
            throw new MallException("微信退款失败2！" + responseVo.getErrCodeDes());
        }
        System.out.println("================微信申请退款结束=========================");
        System.out.println("xml = " + xml);
        return responseVo;
    }
}

