package com.hdkj.mall.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.Constants;
import com.constants.SysConfigConstants;
import com.exception.MallException;
import com.github.pagehelper.PageHelper;
import com.hdkj.mall.system.model.SystemAdmin;
import com.hdkj.mall.system.service.SystemGroupDataService;
import com.hdkj.mall.validatecode.service.ValidateCodeService;
import com.hdkj.mall.authorization.manager.TokenManager;
import com.hdkj.mall.system.model.SystemRole;
import com.hdkj.mall.system.response.SystemAdminResponse;
import com.hdkj.mall.system.service.SystemConfigService;
import com.hdkj.mall.system.service.SystemRoleService;
import com.hdkj.mall.user.model.UserToken;
import com.utils.MallUtil;
import com.utils.ValidateFormUtil;
import com.hdkj.mall.authorization.model.TokenModel;
import com.hdkj.mall.system.dao.SystemAdminDao;
import com.hdkj.mall.system.request.SystemAdminAddRequest;
import com.hdkj.mall.system.request.SystemAdminLoginRequest;
import com.hdkj.mall.system.request.SystemAdminRequest;
import com.hdkj.mall.system.request.SystemRoleSearchRequest;
import com.hdkj.mall.system.response.SystemGroupDataAdminLoginBannerResponse;
import com.hdkj.mall.system.service.SystemAdminService;
import com.hdkj.mall.user.service.UserTokenService;
import com.hdkj.mall.validatecode.model.ValidateCode;
import com.hdkj.mall.wechat.response.WeChatAuthorizeLoginGetOpenIdResponse;
import com.hdkj.mall.wechat.service.WeChatService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SystemAdminServiceImpl 接口实现
 */
@Service
public class SystemAdminServiceImpl extends ServiceImpl<SystemAdminDao, SystemAdmin> implements SystemAdminService {

    @Resource
    private SystemAdminDao dao;

    @Autowired
    private SystemRoleService systemRoleService;

    @Resource
    private TokenManager tokenManager;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private WeChatService weChatService;

    @Autowired
    private ValidateCodeService validateCodeService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private SystemGroupDataService systemGroupDataService;


    @Override
    public List<SystemAdminResponse> getList(SystemAdminRequest request, PageParamRequest pageParamRequest){
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带SystemAdminRequest类的多条件查询
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(request,systemAdmin);
//        lambdaQueryWrapper.setEntity(systemAdmin);
        if(StringUtils.isNotBlank(systemAdmin.getAccount())){
            lambdaQueryWrapper.eq(SystemAdmin::getAccount, systemAdmin.getAccount());
        }

        if(null != systemAdmin.getId()){
            lambdaQueryWrapper.eq(SystemAdmin::getId, systemAdmin.getId());
        }
        if(null != systemAdmin.getIsDel()){
            lambdaQueryWrapper.eq(SystemAdmin::getIsDel, systemAdmin.getIsDel());
        }
        if(StringUtils.isNotBlank(systemAdmin.getLastIp())){
            lambdaQueryWrapper.eq(SystemAdmin::getLastIp, systemAdmin.getLastIp());
        }

        if(null != systemAdmin.getLevel()){
            lambdaQueryWrapper.eq(SystemAdmin::getLevel, systemAdmin.getLevel());
        }
        if(null != systemAdmin.getLoginCount()){
            lambdaQueryWrapper.eq(SystemAdmin::getLoginCount, systemAdmin.getLoginCount());
        }
        if(StringUtils.isNotBlank(systemAdmin.getRealName())){
            lambdaQueryWrapper.like(SystemAdmin::getRealName, systemAdmin.getRealName());
            lambdaQueryWrapper.or().like(SystemAdmin::getAccount, systemAdmin.getRealName());
        }
        if(StringUtils.isNotBlank(systemAdmin.getRoles())){
            lambdaQueryWrapper.eq(SystemAdmin::getRoles, systemAdmin.getRoles());
        }
        if(null != systemAdmin.getStatus()){
            lambdaQueryWrapper.eq(SystemAdmin::getStatus, systemAdmin.getStatus());
        }
        List<SystemAdmin> systemAdmins = dao.selectList(lambdaQueryWrapper);
        List<SystemAdminResponse> systemAdminResponses = new ArrayList<>();
        PageParamRequest pageRole = new PageParamRequest();
        pageRole.setLimit(999);
        List<SystemRole> roleList = systemRoleService.getList(new SystemRoleSearchRequest(), pageRole);
        for (SystemAdmin admin : systemAdmins) {
            SystemAdminResponse sar = new SystemAdminResponse();
            BeanUtils.copyProperties(admin, sar);
            sar.setLastTime(admin.getUpdateTime());
            if(StringUtils.isBlank(admin.getRoles())) continue;
            List<Integer> roleIds = MallUtil.stringToArrayInt(admin.getRoles());
            List<String> roleNames = new ArrayList<>();
            for (Integer roleId : roleIds) {
                List<SystemRole> hasRoles = roleList.stream().filter(e -> e.getId().equals(roleId)).collect(Collectors.toList());
                if(hasRoles.size()> 0){
                    roleNames.add(hasRoles.stream().map(SystemRole::getRoleName).collect(Collectors.joining(",")));
                }
            }
            sar.setRoleNames(StringUtils.join(roleNames,","));
            systemAdminResponses.add(sar);
        }
        return systemAdminResponses;
    }

    @Override
    public SystemAdminResponse login(SystemAdminLoginRequest systemAdminLoginRequest, String ip) throws Exception {
        // 判断验证码
        ValidateCode validateCode = new ValidateCode(systemAdminLoginRequest.getKey(), systemAdminLoginRequest.getCode());
        boolean codeCheckResult = validateCodeService.check(validateCode);
        if(!codeCheckResult) throw new MallException("验证码不正确");
        // 执行登录
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAdmin::getAccount, systemAdminLoginRequest.getAccount());
        SystemAdmin systemAdmin = dao.selectOne(lambdaQueryWrapper);
        if(null == systemAdmin){
            throw new MallException("用户不存在");
        }
        // base64加密获取真正密码
        String encryptPassword = MallUtil.encryptPassword(systemAdminLoginRequest.getPwd(), systemAdminLoginRequest.getAccount());
        if(!systemAdmin.getPwd().equals(encryptPassword)){
            throw new MallException("账号或者密码不正确");
        }
        if(!systemAdmin.getStatus()){
            throw new MallException("用户已经被禁用");
        }
        if(systemAdmin.getIsDel()){
            throw new MallException("用户已经被删除");
        }

        TokenModel tokenModel = tokenManager.createToken(systemAdmin.getAccount(), systemAdmin.getId().toString(), TokenModel.TOKEN_REDIS);
        SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
        systemAdminResponse.setToken(tokenModel.getToken());
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);

        //更新最后登录信息
        systemAdmin.setLoginCount(systemAdmin.getLoginCount() + 1);
        systemAdmin.setLastIp(ip);
        updateById(systemAdmin);

        return systemAdminResponse;
    }

    @Override
    public Boolean logout(String token) throws Exception {
        tokenManager.deleteToken(token, TokenModel.TOKEN_REDIS);
        return true;
    }

    /**
     * 管理员详情
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public SystemAdminResponse getInfo(SystemAdminRequest request) throws Exception {
        QueryWrapper queryWrapper = new QueryWrapper();
//        queryWrapper.eq("account", request.getAccount());
//        queryWrapper.eq("account", request.getAccount());
        queryWrapper.eq("id", request.getId());
        SystemAdmin systemAdmin = dao.selectOne(queryWrapper);

        if(null == systemAdmin || systemAdmin.getId() < 0){
            return null;
        }
        SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
        BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
        return systemAdminResponse;
    }

    /**
     * 新增管理员
     * @param systemAdminAddRequest
     * @return
     */
    @Override
    public SystemAdminResponse saveAdmin(SystemAdminAddRequest systemAdminAddRequest) {
        try {
            // 管理员名称唯一校验
            Integer result = checkAccount(systemAdminAddRequest.getAccount());
            if (result > 0) {
                throw new MallException("管理员已存在");
            }

            // 如果有手机号，校验手机号
            if (StrUtil.isNotBlank(systemAdminAddRequest.getPhone())) {
                ValidateFormUtil.isPhoneException(systemAdminAddRequest.getPhone());
            }

            SystemAdmin systemAdmin = new SystemAdmin();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdmin);

            // 执行新增管理员操作
            String pwd = MallUtil.encryptPassword(systemAdmin.getPwd(), systemAdmin.getAccount());
            systemAdminAddRequest.setPwd(pwd); // 设置为加密后的密码
            systemAdmin.setPwd(pwd);
            SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
            BeanUtils.copyProperties(systemAdminAddRequest, systemAdminResponse);
            if(dao.insert(systemAdmin) <= 0){
                throw new MallException("新增管理员失败");
            }
            return systemAdminResponse;

        }catch (Exception e){
            throw new MallException("新增管理员异常 " + e.getMessage());
        }

    }

    private Integer checkAccount(String account) {
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAdmin::getAccount, account);
        return dao.selectCount(lambdaQueryWrapper);

    }

    @Override
    public SystemAdminResponse updateAdmin(SystemAdminRequest systemAdminRequest) throws Exception {
        SystemAdminResponse systemAdminResponseExsit = getInfo(systemAdminRequest);
        if(null == systemAdminResponseExsit){
            throw new MallException("管理员不存在");
        }

        // 如果有手机号，校验手机号
        if (StrUtil.isNotBlank(systemAdminRequest.getPhone())) {
            ValidateFormUtil.isPhoneException(systemAdminRequest.getPhone());
        }

        SystemAdmin systemAdmin = new SystemAdmin();
        BeanUtils.copyProperties(systemAdminRequest, systemAdmin);
        if(null != systemAdminRequest.getPwd()){
            String pwd = MallUtil.encryptPassword(systemAdminRequest.getPwd(), systemAdminRequest.getAccount());
            systemAdmin.setPwd(pwd);
        }
        if(dao.updateById(systemAdmin) > 0){
            SystemAdminResponse systemAdminResponse = new SystemAdminResponse();
            BeanUtils.copyProperties(systemAdmin, systemAdminResponse);
            return systemAdminResponse;
        }
        throw new MallException("更新管理员异常");
    }

    @Override
    public SystemAdminResponse getInfoByToken(String token) throws Exception{
        Boolean tokenExsit = tokenManager.checkToken(token, TokenModel.TOKEN_REDIS);
        if(!tokenExsit){
            throw new MallException("当前token无效");
        }
        TokenModel tokenModel = tokenManager.getToken(token, TokenModel.TOKEN_REDIS);
        SystemAdminRequest systemAdminRequest = new SystemAdminRequest();
//        systemAdminRequest.setAccount(tokenModel.getUserNo());
        systemAdminRequest.setId(tokenModel.getUserId());
        return getInfo(systemAdminRequest);
    }

    /**
     * 获取当前用户id
     * @author Mr.Zhou
     * @since 2022-04-28
     * @return Integer
     */
    @Override
    public Integer getAdminId() {
        return Integer.parseInt(tokenManager.getLocalInfoException("id"));
    }

    /**
     * 获取当前用户信息
     * @author Mr.Zhou
     * @since 2022-04-28
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getInfo() {
        return getInfo(getAdminId());
    }

    /**
     * 获取用户信息
     * @author Mr.Zhou
     * @since 2022-04-28
     * @return SystemAdmin
     */
    @Override
    public SystemAdmin getInfo(Integer adminId) {
        return getById(adminId);
    }

    /**
     * 绑定微信
     * @author Mr.Zhou
     * @since 2022-04-28
     * @return SystemAdmin
     */
    public void bind(String code, Integer adminId) {
        try{
            //通过code获取用户信息
            WeChatAuthorizeLoginGetOpenIdResponse response = weChatService.authorizeLogin(code);
            UserToken userToken = userTokenService.getByOpenidAndType(response.getOpenId(), Constants.THIRD_ADMIN_LOGIN_TOKEN_TYPE_PUBLIC);
            if(null == userToken){
                userTokenService.bind(response.getOpenId(), Constants.THIRD_ADMIN_LOGIN_TOKEN_TYPE_PUBLIC, adminId);
            }
        }catch (Exception e){
            throw new MallException("绑定失败：" + e.getMessage());
        }
    }

    /**
     * 修改后台管理员状态
     * @param id
     * @param status
     * @return
     */
    @Override
    public Boolean updateStatus(Integer id, Boolean status) {
        SystemAdmin systemAdmin = getById(id);
        if (ObjectUtil.isNull(systemAdmin)) {
            throw new MallException("用户不存在");
        }
        if (systemAdmin.getStatus().equals(status)) {
            return true;
        }
        systemAdmin.setStatus(status);
        return updateById(systemAdmin);
    }

    @Override
    public HashMap<Integer, SystemAdmin> getMapInId(List<Integer> adminIdList) {
        HashMap<Integer, SystemAdmin> map = new HashMap<>();
        if(adminIdList.size() < 1){
            return map;
        }
        LambdaQueryWrapper<SystemAdmin> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.in(SystemAdmin::getId, adminIdList);
        List<SystemAdmin> systemAdminList = dao.selectList(lambdaQueryWrapper);
        if(systemAdminList.size() < 1){
            return map;
        }
        for (SystemAdmin systemAdmin : systemAdminList) {
            map.put(systemAdmin.getId(), systemAdmin);
        }
        return map;
    }

    /**
     * 修改后台管理员是否接收状态
     * @param id 管理员id
     * @return Boolean
     */
    @Override
    public Boolean updateIsSms(Integer id) {
        SystemAdmin systemAdmin = getById(id);
        if (ObjectUtil.isNull(systemAdmin)) {
            throw new MallException("后台管理员不存在!");
        }
        if (StrUtil.isBlank(systemAdmin.getPhone())) {
            throw new MallException("请先为管理员添加手机号!");
        }

        systemAdmin.setIsSms(!systemAdmin.getIsSms());
        return updateById(systemAdmin);
    }

    /**
     * 获取可以接收短信的管理员
     * @return List
     */
    @Override
    public List<SystemAdmin> findIsSmsList() {
        LambdaQueryWrapper<SystemAdmin> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAdmin::getStatus, true);
        lqw.eq(SystemAdmin::getIsDel, false);
        lqw.eq(SystemAdmin::getIsSms, true);
        List<SystemAdmin> list = dao.selectList(lqw);
        if (CollUtil.isEmpty(list)) {
            return list;
        }
        return list.stream().filter(i -> StrUtil.isNotBlank(i.getPhone())).collect(Collectors.toList());
    }

    /**
     * 获取登录页图片
     * @return
     */
    @Override
    public Map<String, Object> getLoginPic() {
        Map<String, Object> map = new HashMap<>();
        //背景图
        map.put("backgroundImage", systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_BACKGROUND_IMAGE));
        //logo
        map.put("logo", systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LEFT_TOP));
        map.put("loginLogo", systemConfigService.getValueByKey(SysConfigConstants.CONFIG_KEY_ADMIN_LOGIN_LOGO_LOGIN));
        //轮播图
        List<SystemGroupDataAdminLoginBannerResponse> bannerList = systemGroupDataService.getListByGid(Constants.GROUP_DATA_ID_ADMIN_LOGIN_BANNER_IMAGE_LIST, SystemGroupDataAdminLoginBannerResponse.class);
        map.put("banner", bannerList);
        return map;
    }

}

