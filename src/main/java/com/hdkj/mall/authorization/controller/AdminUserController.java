package com.hdkj.mall.authorization.controller;

import com.common.CheckAdminToken;
import com.common.CommonResult;
import com.hdkj.mall.system.request.SystemAdminLoginRequest;
import com.hdkj.mall.system.response.SystemAdminResponse;
import com.hdkj.mall.system.service.SystemAdminService;
import com.utils.MallUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Admin 平台用户
 */
@Slf4j
@RestController
@RequestMapping("api/admin")
@Api(tags = "Admin 平台用户")
public class AdminUserController {

    @Autowired
    private SystemAdminService systemAdminService;

    @Autowired
    private CheckAdminToken checkAdminToken;

    @ApiOperation(value="PC登录")
    @PostMapping(value = "/login", produces = "application/json")
    public CommonResult<SystemAdminResponse> SystemAdminLogin(@RequestBody @Validated SystemAdminLoginRequest systemAdminLoginRequest, HttpServletRequest request) throws Exception {
        String ip = MallUtil.getClientIp(request);
        SystemAdminResponse systemAdminResponse = systemAdminService.login(systemAdminLoginRequest, ip);
//        if(StringUtils.isNotBlank(systemAdminLoginRequest.getWxCode())){
//            systemAdminService.bind(systemAdminLoginRequest.getWxCode(), systemAdminResponse.getId());
//        }
        return CommonResult.success(systemAdminResponse, "login success");
    }

    @ApiOperation(value="PC登出")
    @GetMapping(value = "/logout")
    public CommonResult<SystemAdminResponse> SystemAdminLogout(HttpServletRequest request) throws Exception {
        String token = checkAdminToken.getTokenFormRequest(request);
        systemAdminService.logout(token);
        return CommonResult.success("logout success");
    }

    @ApiOperation(value="获取用户详情")
    @GetMapping(value = "/getAdminInfoByToken")
    public CommonResult<SystemAdminResponse> getAdminInfo(HttpServletRequest request) throws Exception{
        String token = checkAdminToken.getTokenFormRequest(request);
        SystemAdminResponse systemAdminResponse = systemAdminService.getInfoByToken(token);

        return CommonResult.success(systemAdminResponse);
    }

    /**
     * 获取登录页图片
     * @return Map<String, Object>
     */
    @ApiOperation(value = "获取登录页图片")
    @RequestMapping(value = "/getLoginPic", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> getLoginPic(){
        return CommonResult.success(systemAdminService.getLoginPic());
    }

//    /**
//     * 微信登录公共号授权登录
//     * @author Mr.Zhou
//     * @since 2022-05-25
//     */
//    @ApiOperation(value = "微信登录公共号授权登录")
//    @RequestMapping(value = "/authorize/login", method = RequestMethod.GET)
//    public CommonResult<SystemAdminResponse> login(@RequestParam(value = "code") String code, HttpServletRequest request) throws Exception {
//        return CommonResult.success(systemAdminService.weChatAuthorizeLogin(code, MallUtil.getClientIp(request)));
//    }
//
//    /**
//     * 解绑微信
//     * @author Mr.Zhou
//     * @since 2022-05-25
//     */
//    @ApiOperation(value = "解绑微信")
//    @RequestMapping(value = "/unbind", method = RequestMethod.GET)
//    public CommonResult<Boolean> bind(){
//        return CommonResult.success(systemAdminService.unBind());
//    }
}
