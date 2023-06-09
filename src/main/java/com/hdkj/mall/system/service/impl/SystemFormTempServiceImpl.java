package com.hdkj.mall.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.exception.MallException;
import com.github.pagehelper.PageHelper;
import com.hdkj.mall.system.model.SystemFormTemp;
import com.hdkj.mall.system.vo.SystemConfigFormItemConfigRegListVo;
import com.hdkj.mall.system.vo.SystemConfigFormVo;
import com.hdkj.mall.system.dao.SystemFormTempDao;
import com.hdkj.mall.system.request.SystemFormCheckRequest;
import com.hdkj.mall.system.request.SystemFormItemCheckRequest;
import com.hdkj.mall.system.request.SystemFormTempSearchRequest;
import com.hdkj.mall.system.service.SystemFormTempService;
import com.utils.ValidateFormUtil;
import com.hdkj.mall.system.vo.SystemConfigFormItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * SystemFormTempServiceImpl 接口实现
 */
@Service
public class SystemFormTempServiceImpl extends ServiceImpl<SystemFormTempDao, SystemFormTemp> implements SystemFormTempService {

    @Resource
    private SystemFormTempDao dao;


    /**
    * 列表
    * @param request 请求参数
    * @param pageParamRequest 分页类参数
    * @author Mr.Zhou
    * @since 2022-05-15
    * @return List<SystemFormTemp>
    */
    @Override
    public List<SystemFormTemp> getList(SystemFormTempSearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());

        //带 SystemFormTemp 类的多条件查询
        LambdaQueryWrapper<SystemFormTemp> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isBlank(request.getKeywords())){
            lambdaQueryWrapper.eq(SystemFormTemp::getId, request.getKeywords()).
                    or().like(SystemFormTemp::getName, request.getKeywords()).
                    or().like(SystemFormTemp::getInfo, request.getKeywords());
        }
        lambdaQueryWrapper.orderByDesc(SystemFormTemp::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 验证item规则
     * @param systemFormCheckRequest SystemFormCheckRequest 表单数据提交
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @Override
    public void checkForm(SystemFormCheckRequest systemFormCheckRequest){
        //循环取出item数据， 组合成 key => val 的map格式
        HashMap<String, String> map = new HashMap<>();
        for (SystemFormItemCheckRequest systemFormItemCheckRequest : systemFormCheckRequest.getFields()) {
            map.put(systemFormItemCheckRequest.getName(), systemFormItemCheckRequest.getValue());
        }

        //取出表单模型的数据
        SystemFormTemp formTemp = getById(systemFormCheckRequest.getId());


        //解析表单规则进行验证
        SystemConfigFormVo systemConfigFormVo;
        try{
            systemConfigFormVo =  JSONObject.parseObject(formTemp.getContent(), SystemConfigFormVo.class);
        }catch (Exception e){
            throw new MallException("模板表单 【" + formTemp.getName() + "】 的内容不是正确的JSON格式！");
        }

        SystemConfigFormItemVo systemConfigFormItemVo;

        for (String item : systemConfigFormVo.getFields()) {

            systemConfigFormItemVo = JSONObject.parseObject(item, SystemConfigFormItemVo.class);
            String model = systemConfigFormItemVo.get__vModel__(); //字段 name

            if(systemConfigFormItemVo.get__config__().getRequired() && map.get(model).equals("")){
                throw new MallException(systemConfigFormItemVo.get__config__().getLabel() + "不能为空！");
            }

            //正则验证
            checkRule(systemConfigFormItemVo.get__config__().getRegList(), map.get(model),  systemConfigFormItemVo.get__config__().getLabel());
        }
    }

    /**
     * 验证item规则
     * @param regList List<SystemConfigFormItemConfigRegListVo 正则表达式列表
     * @param value String 验证的值
     * @param name String 提示语字段名称
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    private void checkRule(List<SystemConfigFormItemConfigRegListVo> regList, String value, String name){
        if(regList.size() > 0){
            for (SystemConfigFormItemConfigRegListVo systemConfigFormItemConfigRegListVo : regList) {
                if(!ValidateFormUtil.regular(value, name, systemConfigFormItemConfigRegListVo.getPattern())){
                    throw new MallException(systemConfigFormItemConfigRegListVo.getMessage());
                }
            }
        }
    }
}

