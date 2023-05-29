package com.hdkj.mall.front.controller;


import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.exception.MallException;
import com.github.pagehelper.PageInfo;
import com.hdkj.mall.category.model.Answer;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.finance.request.UserExtractRequest;
import com.hdkj.mall.front.service.UserCenterService;
import com.hdkj.mall.system.model.SystemUserLevel;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.system.service.SystemGroupDataService;
import com.hdkj.mall.user.model.*;
import com.hdkj.mall.user.response.UserLjhsOrderResponse;
import com.hdkj.mall.user.response.UserOthersOrderResponse;
import com.hdkj.mall.user.service.UserLjhsOrderService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.hdkj.mall.category.vo.CategoryVo;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.user.request.UserLjhsOrdeRequest;
import com.hdkj.mall.user.request.UserOthersOrdeRequest;
import com.hdkj.mall.user.response.UserLjhsOrderNum;
import com.hdkj.mall.user.response.UserLjhsOrderReplyResponse;
import com.hdkj.mall.user.service.UserService;
import com.hdkj.mall.front.request.*;
import com.hdkj.mall.front.response.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 -- 用户中心

 */
@Slf4j
@RestController("FrontUserController")
@RequestMapping("api/front")
@Api(tags = "用户 -- 用户中心")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;

    @Autowired
    private UserCenterService userCenterService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserLjhsOrderService userLjhsOrderService;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 修改密码
     */
    @ApiOperation(value = "手机号修改密码")
    @RequestMapping(value = "/register/reset", method = RequestMethod.POST)
    public CommonResult<Boolean> password(@RequestBody @Validated PasswordRequest request){
        return CommonResult.success(userService.password(request));
    }

    /**
     * 修改个人资料
     */
    @ApiOperation(value = "修改个人资料")
    @RequestMapping(value = "/user/edit", method = RequestMethod.POST)
    public CommonResult<Boolean> personInfo(@RequestBody @Validated UserEditRequest request){
        User user = userService.getInfo();
        user.setAvatar(systemAttachmentService.clearPrefix(request.getAvatar()));
        user.setNickname(request.getNickname());
        return CommonResult.success(userService.updateById(user));
    }
    /**
     * 修改个人背景
     */
    @ApiOperation(value = "修改个人背景")
    @RequestMapping(value = "/user/editBg", method = RequestMethod.POST)
    public CommonResult<Boolean> editBg(@RequestBody @Validated UserBgEditRequest request){
        User user =null;
        if(request.getUid() ==null || request.getUid()==0){
            user = userService.getInfo();
        }else{
            user = userService.getById(request.getUid());
        }

        if(request.getMark()!=null && !request.getMark().equals("")){
            user.setMark(systemAttachmentService.clearPrefix(request.getMark()));
        }
        return CommonResult.success(userService.updateById(user));
    }



    /**
     * 个人中心-用户信息
     */
    @ApiOperation(value = "个人中心-用户信息")
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public CommonResult<UserCenterResponse> getUserCenter(){
        return CommonResult.success(userService.getUserCenter());
    }
    /**
     * 个人中心-用户信息
     */
    @ApiOperation(value = "个人中心-用户信息")
    @RequestMapping(value = "/user/byId", method = RequestMethod.GET)
    public CommonResult<UserCenterResponse> getUserCenterById(Integer id){
        return CommonResult.success(userService.getUserCenterByid(id));
    }

    /**
     * 个人中心-用户信息
     */
    @ApiOperation(value = "个人中心-用户信息")
    @RequestMapping(value = "/user/byAdminId", method = RequestMethod.GET)
    public CommonResult<CategoryVo> getAdminInfoById(Integer id){
        if(id ==null){
            throw new MallException("参数错误");
        }
        return CommonResult.success(userService.getAdminInfoById(id));
    }
    /**
     * 修改个人背景
     */
    @ApiOperation(value = "修改个人背景")
    @RequestMapping(value = "/user/service/editAdminBg", method = RequestMethod.POST)
    public CommonResult<Boolean> editAdminBg(@RequestBody @Validated UserBgEditRequest request){
        if(request.getUid() ==null || request.getUid()==0){
            throw new MallException("缺少参数");
        }
        Category Category =categoryService.getById(request.getUid());
        if(Category ==null){
            throw new MallException("数据不存在");
        }

        if(request.getMark()!=null && !request.getMark().equals("")){
            Category.setUrl(systemAttachmentService.clearPrefix(request.getMark()));
        }
        return CommonResult.success(categoryService.updateById(Category));
    }
    /**
     * 换绑手机号校验
     */
    @ApiOperation(value = "换绑手机号校验")
    @RequestMapping(value = "update/binding/verify", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhoneVerify(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhoneVerify(request));
    }

    /**
     * 绑定手机号
     */
    @ApiOperation(value = "换绑手机号")
    @RequestMapping(value = "update/binding", method = RequestMethod.POST)
    public CommonResult<Boolean> updatePhone(@RequestBody @Validated UserBindingPhoneUpdateRequest request){
        return CommonResult.success(userService.updatePhone(request));
    }

    /**
     * 用户中心菜单
     */
    @ApiOperation(value = "获取个人中心菜单")
    @RequestMapping(value = "/menu/user", method = RequestMethod.GET)
    public CommonResult<HashMap<String, Object>> getMenuUser(){
        return CommonResult.success(systemGroupDataService.getMenuUser());
    }

    /**
     * 推广数据接口(昨天的佣金 累计提现金额 当前佣金)
     */
    @ApiOperation(value = "推广数据接口(昨天的佣金 累计提现金额 当前佣金)")
    @RequestMapping(value = "/commission", method = RequestMethod.GET)
    public CommonResult<UserCommissionResponse> getCommission(){
        return CommonResult.success(userCenterService.getCommission());
    }

    /**
     * 推广佣金明细
     */
    @ApiOperation(value = "推广佣金明细")
    @RequestMapping(value = "/spread/commission/detail", method = RequestMethod.GET)
    public CommonResult<CommonPage<SpreadCommissionDetailResponse>> getSpreadCommissionDetail(@Validated PageParamRequest pageParamRequest){
        PageInfo<SpreadCommissionDetailResponse> commissionDetail = userCenterService.getSpreadCommissionDetail(pageParamRequest);
        return CommonResult.success(CommonPage.restPage(commissionDetail));
    }

    /**
     * 推广佣金/提现总和
     */
    @ApiOperation(value = "推广佣金/提现总和")
    @RequestMapping(value = "/spread/count/{type}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "type", value = "类型 佣金类型3=佣金,4=提现", allowableValues = "range[3,4]", dataType = "int")
    public CommonResult<Map<String, BigDecimal>> getSpreadCountByType(@PathVariable int type){
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("count", userCenterService.getSpreadCountByType(type));
        return CommonResult.success(map);
    }

    /**
     * 提现申请
     */
    @ApiOperation(value = "提现申请")
    @RequestMapping(value = "/extract/cash", method = RequestMethod.POST)
    public CommonResult<Boolean> extractCash(@RequestBody @Validated UserExtractRequest request){
        return CommonResult.success(userCenterService.extractCash(request));
    }

    /**
     * 提现记录
     */
    @ApiOperation(value = "提现记录")
    @RequestMapping(value = "/extract/record", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserExtractRecordResponse>> getExtractRecord(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getExtractRecord(pageParamRequest)));
    }

    /**
     * 提现用户信息
     */
    @ApiOperation(value = "提现用户信息")
    @RequestMapping(value = "/extract/user", method = RequestMethod.GET)
    public CommonResult<UserExtractCashResponse> getExtractUser(){
        return CommonResult.success(userCenterService.getExtractUser());
    }

    /**
     * 提现银行
     */
    @ApiOperation(value = "提现银行/提现最低金额")
    @RequestMapping(value = "/extract/bank", method = RequestMethod.GET)
    public CommonResult<List<String>> getExtractBank(){
        return CommonResult.success(userCenterService.getExtractBank());
    }

    /**
     * 会员等级列表
     */
    @ApiOperation(value = "会员等级列表")
    @RequestMapping(value = "/user/level/grade", method = RequestMethod.GET)
    public CommonResult<List<SystemUserLevel>> getUserLevelList(){
        return CommonResult.success(userCenterService.getUserLevelList());
    }

    /**
     * 推广人统计
     */
    @ApiOperation(value = "推广人统计")
    @RequestMapping(value = "/spread/people/count", method = RequestMethod.GET)
    public CommonResult<UserSpreadPeopleResponse>  getSpreadPeopleCount(){
        return CommonResult.success(userCenterService.getSpreadPeopleCount());
    }

    /**
     * 推广人列表
     */
    @ApiOperation(value = "推广人列表")
    @RequestMapping(value = "/spread/people", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserSpreadPeopleItemResponse>> getSpreadPeopleList(@Validated UserSpreadPeopleRequest request, @Validated PageParamRequest pageParamRequest) {
        List<UserSpreadPeopleItemResponse> spreadPeopleList = userCenterService.getSpreadPeopleList(request, pageParamRequest);
        CommonPage<UserSpreadPeopleItemResponse> commonPage = CommonPage.restPage(spreadPeopleList);
        return CommonResult.success(commonPage);
    }

    /**
     * 用户积分信息
     */
    @ApiOperation(value = "用户积分信息")
    @RequestMapping(value = "/integral/user", method = RequestMethod.GET)
    public CommonResult<IntegralUserResponse> getIntegralUser(){
        return CommonResult.success(userCenterService.getIntegralUser());
    }

    /**
     * 积分记录
     */
    @ApiOperation(value = "积分记录")
    @RequestMapping(value = "/integral/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserIntegralRecord>> getIntegralList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserIntegralRecordList(pageParamRequest)));
    }

    /**
     * 经验记录
     */
    @ApiOperation(value = "经验记录")
    @RequestMapping(value = "/user/expList", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserBill>> getExperienceList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(CommonPage.restPage(userCenterService.getUserExperienceList(pageParamRequest)));
    }

    /**
     * 用户资金统计
     */
    @ApiOperation(value = "用户资金统计")
    @RequestMapping(value = "/user/balance", method = RequestMethod.GET)
    public CommonResult<UserBalanceResponse>  getUserBalance(){
        return CommonResult.success(userCenterService.getUserBalance());
    }

    /**
     * 推广订单
     */
    @ApiOperation(value = "推广订单")
    @RequestMapping(value = "/spread/order", method = RequestMethod.GET)
    public CommonResult<UserSpreadOrderResponse>  getSpreadOrder(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadOrder(pageParamRequest));
    }

    /**
     * 推广人排行
     * @return List<User>
     */
    @ApiOperation(value = "推广人排行")
    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopSpreadPeopleListByDate(@RequestParam(required = false) String type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopSpreadPeopleListByDate(type, pageParamRequest));
    }

    /**
     * 佣金排行
     * @return 优惠券集合
     */
    @ApiOperation(value = "佣金排行")
    @RequestMapping(value = "/brokerage_rank", method = RequestMethod.GET)
    public CommonResult<List<User>> getTopBrokerageListByDate(@RequestParam String type, @Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getTopBrokerageListByDate(type, pageParamRequest));
    }

    /**
     * 当前用户在佣金排行第几名
     * @return 优惠券集合
     */
    @ApiOperation(value = "当前用户在佣金排行第几名")
    @RequestMapping(value = "/user/brokerageRankNumber", method = RequestMethod.GET)
    public CommonResult<Integer> getNumberByTop(@RequestParam String type){
        return CommonResult.success(userCenterService.getNumberByTop(type));
    }

    /**
     * 海报背景图
     */
    @ApiOperation(value = "推广海报图")
    @RequestMapping(value = "/user/spread/banner", method = RequestMethod.GET)
    public CommonResult<List<UserSpreadBannerResponse>>  getSpreadBannerList(@Validated PageParamRequest pageParamRequest){
        return CommonResult.success(userCenterService.getSpreadBannerList(pageParamRequest));
    }

    /**
     * 绑定推广关系（登录状态）
     * @param spreadPid 推广id
     * @return 绑定结果
     */
    @ApiOperation(value = "绑定推广关系（登录状态）")
    @RequestMapping(value = "/user/bindSpread", method = RequestMethod.GET)
    public CommonResult<Boolean> bindsSpread(Integer spreadPid){
        userService.bindSpread(spreadPid);
        return CommonResult.success();
    }

    /**
     * 查询分类表信息
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "获取tree结构的列表")
    @RequestMapping(value = "/user/service/list/tree", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value="类型ID | 类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置", example = "1"),
            @ApiImplicitParam(name="status", value="-1=全部，0=未生效，1=已生效", example = "1"),
            @ApiImplicitParam(name="name", value="模糊搜索", example = "电视")
    })
    public CommonResult<List<CategoryTreeVo>> getListTree(@RequestParam(name = "type") Integer type,
                                                          @RequestParam(name = "status") Integer status,
                                                          @RequestParam(name = "name", required = false) String name){
        List<CategoryTreeVo> listTree = categoryService.getListTree(type,status,name);
        return CommonResult.success(listTree);
    }


    /**
     * 用户垃圾代仍时间段
     */
    @ApiOperation(value = "用户垃圾代仍时间段")
    @RequestMapping(value = "/user/service/ljdrtime", method = RequestMethod.GET)
    public CommonResult<Map<String,Object>> ljdrtime(){
        int ljdr_stime= 7;
        int ljdr_etime= 22;
        try {
            String ljdrtime = systemConfigService.getValueByKey(Constants.CONFIG_YY_TIME);
            if(ljdrtime !=null && !ljdrtime.equals("")){
                ljdr_stime = Integer.valueOf(ljdrtime.split(",")[0].split(":")[0]);
                ljdr_etime = Integer.valueOf(ljdrtime.split(",")[1].split(":")[0]);
            }
        }catch (Exception e){
        }
        Map<String,Object> map = new HashMap<>();
        map.put("ljdr_stime", ljdr_stime);
        map.put("ljdr_etime",ljdr_etime);
        return CommonResult.success(map);
    }
    /**
     * 用户垃回收仍时间段
     */
    @ApiOperation(value = "用户垃圾回收时间段")
    @RequestMapping(value = "/user/service/ljhstime", method = RequestMethod.GET)
    public CommonResult<Map<String,Integer>> ljhstime(){

        int ljhs_stime= 7;
        int ljhs_etime= 22;
        try {
            String ljdrtime = systemConfigService.getValueByKey(Constants.CONFIG_YY_TIME);
            if(ljdrtime !=null && !ljdrtime.equals("")){
                ljhs_stime = Integer.valueOf(ljdrtime.split(",")[0].split(":")[0]);
                ljhs_etime = Integer.valueOf(ljdrtime.split(",")[1].split(":")[0]);
            }
        }catch (Exception e){
        }
        Map<String,Integer> map = new HashMap<>();
        map.put("ljhs_stime",ljhs_stime);
        map.put("ljhs_etime",ljhs_etime);
        return CommonResult.success(map);
    }
    /**
     * 用户垃回收仍时间段
     */
    @ApiOperation(value = "用户维修服务时间段")
    @RequestMapping(value = "/user/service/wxfwsjfw", method = RequestMethod.GET)
    public CommonResult<Map<String,Integer>> wxfwsjfw(){

        int wxfw_stime= 7;
        int wxfw_etime= 22;
        try {
            String wxfwtime = systemConfigService.getValueByKey(Constants.CONFIG_YY_TIME);
            if(wxfwtime !=null && !wxfwtime.equals("")){
                wxfw_stime = Integer.valueOf(wxfwtime.split(",")[0].split(":")[0]);
                wxfw_etime = Integer.valueOf(wxfwtime.split(",")[1].split(":")[0]);
            }
        }catch (Exception e){
        }
        Map<String,Integer> map = new HashMap<>();
        map.put("wxfw_stime",wxfw_stime);
        map.put("wxfw_etime",wxfw_etime);
        return CommonResult.success(map);
    }
    /**
     * 用户服务向导图
     */
    @ApiOperation(value = "用户服务向导图")
    @RequestMapping(value = "/user/service/fwxdt", method = RequestMethod.GET)
    public CommonResult<Map<String,String>> fwxdt(){

        String laundry_logo = systemConfigService.getValueByKey(Constants.CONFIG_laundry_logo);
        String recycle_logo = systemConfigService.getValueByKey(Constants.CONFIG_recycle_logo);
        String maintain_logo = systemConfigService.getValueByKey(Constants.CONFIG_maintain_logo);
        Map<String,String> map = new HashMap<>();
        map.put("laundry_logo",laundry_logo);
        map.put("recycle_logo",recycle_logo);
        map.put("maintain_logo",maintain_logo);
        return CommonResult.success(map);
    }

    /**
     * 用户垃圾代仍订单创建
     */
    @ApiOperation(value = "用户垃圾代仍订单创建")
    @RequestMapping(value = "/user/service/createLjdrOder", method = RequestMethod.POST)
    public CommonResult<Boolean> createLjdrOder(@RequestBody UserLjhsOrdeRequest userLjhsOrdeRequest){
        return CommonResult.success(userLjhsOrderService.createLjdrOder(userLjhsOrdeRequest));
    }
    /**
     * 用户垃圾回收订单创建
     */
    @ApiOperation(value = "用户垃圾回收订单创建")
    @RequestMapping(value = "/user/service/createLjhsOder", method = RequestMethod.POST)
    public CommonResult<Boolean> createLjhsOder(@RequestBody  UserLjhsOrdeRequest userLjhsOrdeRequest){
        return CommonResult.success(userLjhsOrderService.createLjdrOder(userLjhsOrdeRequest));
    }
    /**
     * 用户垃圾订单查询
     */
    @ApiOperation(value = "用户垃圾代仍订单查询")
    @RequestMapping(value = "/user/service/listLjdrOrder", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserLjhsOrderResponse>> listLjdrOrder(@Validated UserLjhsOrdeRequest request
            , @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(userLjhsOrderService.listLjdrOrder(request,pageRequest));
    }

    /**
     * 用户垃圾代仍订单数量查询
     */
    @ApiOperation(value = "用户垃圾代仍订单数量查询")
    @RequestMapping(value = "/user/service/listLjdrOrderNum", method = RequestMethod.GET)
    public CommonResult<UserLjhsOrderNum> listLjdrOrderNum(@Validated UserLjhsOrdeRequest request){
        return CommonResult.success(userLjhsOrderService.listLjdrOrderNum(request));
    }
    /**
     * 用户垃圾订单详情查询
     */
    @ApiOperation(value = "用户垃圾代仍订单查询")
    @RequestMapping(value = "/user/service/ljdrOrderDetails", method = RequestMethod.GET)
    public CommonResult<UserLjhsOrderResponse> ljdrOrderDetails(@RequestParam(name = "id") Integer id
            , @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(userLjhsOrderService.ljdrOrderDetails(id));
    }

    /**
     * 用户垃圾订单详情查询
     */
    @ApiOperation(value = "用户垃圾代仍订单查询")
    @RequestMapping(value = "/user/service/ljdrOrderDetailsByOrderId", method = RequestMethod.GET)
    public CommonResult<UserLjhsOrderResponse> ljdrOrderDetailsByOrderId(@RequestParam(name = "orderId") String orderId
            , @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(userLjhsOrderService.ljdrOrderDetailsByOrderId(orderId));
    }

    /**
     * 用户垃圾代仍订单更新
     */
    @ApiOperation(value = "用户垃圾代仍订单更新")
    @RequestMapping(value = "/user/service/ljdrOrderUpdate", method = RequestMethod.POST)
    public CommonResult<Boolean> ljdrOrderUpdate(@RequestBody  UserLjhsOrdeRequest userLjhsOrdeRequest){
        return CommonResult.success(userLjhsOrderService.updateLjdrOder(userLjhsOrdeRequest));
    }
    /**
     * 查询分类表信息
     */
    @ApiOperation(value = "获取tree结构的列表")
    @RequestMapping(value = "/user/service/list/tree/typer", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name="type", value="类型ID | 类型，1 产品分类，2 附件分类，3 文章分类， 4 设置分类， 5 菜单分类， 6 配置分类， 7 秒杀配置", example = "1"),
            @ApiImplicitParam(name="status", value="-1=全部，0=未生效，1=已生效", example = "1"),
    })
    public CommonResult<List<CategoryTreeVo>> getListTreeByTyper(@RequestParam(name = "type") Integer type,
                                                                 @RequestParam(name = "status") Integer status,
                                                                 @RequestParam(name = "pid") Integer pid){
        List<CategoryTreeVo> listTree = categoryService.getListTreeByTyper(type,status,pid);
        return CommonResult.success(listTree);
    }

    /**
     * 用户其他创建
     */
    @ApiOperation(value = "用户垃圾回收订单创建")
    @RequestMapping(value = "/user/service/createOthersOder", method = RequestMethod.POST)
    public CommonResult<Boolean> createOthersOder(@RequestBody UserOthersOrdeRequest userOthersOrdeRequest){
        return CommonResult.success(userLjhsOrderService.createOthersOder(userOthersOrdeRequest));
    }

    /**
     * 用户垃圾订单查询
     */
    @ApiOperation(value = "用户其他订单查询")
    @RequestMapping(value = "/user/service/listOthersOrder", method = RequestMethod.GET)
    public CommonResult<CommonPage<UserOthersOrderResponse>> listOthersOrder(@Validated UserOthersOrdeRequest request
            , @ModelAttribute PageParamRequest pageRequest){
        return CommonResult.success(userLjhsOrderService.listOthersOrder(request,pageRequest));
    }
    /**
     * 用户垃圾订单详情查询
     */
    @ApiOperation(value = "用户其他订单查询")
    @RequestMapping(value = "/user/service/otherOrderDetailsById", method = RequestMethod.GET)
    public CommonResult<UserOthersOrderResponse> otherOrderDetailsById(@RequestParam(name = "id") Integer id){
        return CommonResult.success(userLjhsOrderService.otherOrderDetailsByOrderId(id));
    }
    /**
     * 分页显示分类表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/user/service/listAnswer", method = RequestMethod.GET)
    public CommonResult<CommonPage<Answer>>  getListAnswer(@ModelAttribute Answer request, @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<Answer> categoryCommonPage = CommonPage.restPage(categoryService.getListAnswer(request, pageParamRequest));
        return CommonResult.success(categoryCommonPage);
    }

    /**
     * 提现注意事项
     */
    @ApiOperation(value = "提现注意事项")
    @RequestMapping(value = "/user/service/tranfer", method = RequestMethod.GET)
    public CommonResult<UserRechargeResponse> getTranferConfig(){
        return CommonResult.success(userCenterService.getTranferConfig());
    }

    /**
     * 用户垃圾代仍订单更新
     */
    @ApiOperation(value = "用户服务订单评价更新")
    @RequestMapping(value = "/user/service/sericeOrderComment", method = RequestMethod.POST)
    public CommonResult<Boolean> sericeOrderComment(@RequestBody UserLjhsOrderReply userLjhsOrderReply){
        return CommonResult.success(userLjhsOrderService.sericeOrderComment(userLjhsOrderReply));
    }

    /**
     * 用户垃圾代仍订单更新
     */
    @ApiOperation(value = "用户服务订单状态跟踪")
    @RequestMapping(value = "/user/service/listUserLjhsOrderStatus", method = RequestMethod.GET)
    public CommonResult<List<UserLjhsOrderStatus>> listUserLjhsOrderStatus(@RequestParam(name = "orderId") String orderId){
        return CommonResult.success(userLjhsOrderService.listUserLjhsOrderStatus(orderId));
    }

    /**
     * 分页显示评价列表
     */
    @ApiOperation(value = "分页显示评价列表")
    @RequestMapping(value = "/user/service/listSericeOrderComment", method = RequestMethod.GET)
//    @ApiImplicitParam(name = "typeId", value = "类型|0=洗衣,1=回收,2=维修", allowableValues = "range[0,1,2]")
//    @ApiImplicitParam(name = "type", value = "评价等级|0=全部,1=好评,2=中评,3=差评", allowableValues = "range[0,1,2,3]")
    public CommonResult<CommonPage<UserLjhsOrderReplyResponse>>  getListSericeOrderComment(@RequestParam(name = "type") Integer type,@RequestParam(name = "typeId") Integer typeId, @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<UserLjhsOrderReplyResponse> categoryCommonPage = CommonPage.restPage(userLjhsOrderService.getListSericeOrderComment(type,typeId, pageParamRequest));
        return CommonResult.success(categoryCommonPage);
    }

    /**
     * 显示评价条数
     */
    @ApiOperation(value = "显示评价条数")
    @RequestMapping(value = "/user/service/sericeOrderCommentCount", method = RequestMethod.GET)
    public CommonResult<StoreProductReplayCountResponse> sericeOrderCommentCount(@RequestParam(name = "type") Integer type){
        return CommonResult.success(userLjhsOrderService.sericeOrderCommentCount(type));
    }
}



