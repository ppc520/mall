package com.hdkj.mall.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.system.model.SystemRole;
import com.hdkj.mall.system.request.SystemRoleSearchRequest;

import java.util.List;

/**
 * SystemRoleService 接口
 */
public interface SystemRoleService extends IService<SystemRole> {

    List<SystemRole> getList(SystemRoleSearchRequest request, PageParamRequest pageParamRequest);

    /**
     * 根据id集合获取对应权限列表
     * @param ids id集合
     * @return 对应的权限列表
     */
    List<SystemRole> getListInIds(List<Integer> ids);

    Boolean checkAuth(String uri);

    List<CategoryTreeVo> menu();

    Boolean updateStatus(Integer id, Boolean status);
}
