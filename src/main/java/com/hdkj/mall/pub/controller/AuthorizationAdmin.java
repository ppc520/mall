package com.hdkj.mall.pub.controller;

import com.common.CommonResult;
import com.hdkj.mall.system.response.SystemAdminResponse;
import com.hdkj.mall.system.service.SystemAdminService;
import com.utils.MallUtil;
import com.hdkj.mall.system.request.SystemAdminRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Admin管理员 密码操作
 */
@Slf4j
@RestController
@RequestMapping("api/public/auth/test/account")
@Api(tags = "Admin管理员 密码操作")
public class AuthorizationAdmin {

    @Autowired
    private SystemAdminService systemAdminService;

    @ApiOperation(value = "破解密码")
    @RequestMapping(value = "/decode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="account", value="账号"),
            @ApiImplicitParam(name="encodeString", value="加密字符串"),
    })
    public CommonResult<Map> deCodePwd(@RequestParam String account, @RequestParam String encodeString)
            throws Exception {
        SystemAdminResponse systemAdminResponse =
                systemAdminService.getInfo(new SystemAdminRequest().setAccount(account));
        if(null == systemAdminResponse || systemAdminResponse.getId() < 0){
            return CommonResult.failed("account:"+account+"不存在");
        }
        String _password = MallUtil.decryptPassowrd(encodeString, account);
        Map<String,String> result = new HashMap<>();
        result.put("account", account);
        result.put("encodeString", encodeString);
        result.put("password", _password);
        return CommonResult.success(result);
    }

    @ApiOperation(value = "密码加密")
    @RequestMapping(value = "/encode", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name="account", value="账号"),
            @ApiImplicitParam(name="password", value="密码"),
    })
    public CommonResult<Map> encodePwd(@RequestParam String account, @RequestParam String password)
    throws Exception{
        SystemAdminResponse systemAdminResponse =
                systemAdminService.getInfo(new SystemAdminRequest().setAccount(account));
        if(null == systemAdminResponse || systemAdminResponse.getId() < 0){
            return CommonResult.failed("account:"+account+"不存在");
        }
        String encodeString = MallUtil.encryptPassword(password, account);
        Map<String,String> result = new HashMap<>();
        result.put("aAccount", account);
        result.put("encodeString", encodeString);
        result.put("password", password);
        return CommonResult.success(result);
    }

}
