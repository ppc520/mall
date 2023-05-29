package com.hdkj.mall.category.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.PageParamRequest;
import com.hdkj.mall.category.model.Answer;
import com.hdkj.mall.category.request.CategoryRequest;
import com.hdkj.mall.category.request.CategorySearchRequest;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.category.model.Category;

import java.util.HashMap;
import java.util.List;

/**
*   CategoryService 接口
*/
public interface CategoryService extends IService<Category> {
    List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest);

    int delete(Integer id);

    String getPathByPId(Integer pid);

    List<CategoryTreeVo> getListTree(Integer type, Integer status, String name);
    List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList);

    List<Category> getByIds(List<Integer> ids);

    HashMap<Integer, String> getListInId(List<Integer> cateIdList);

    Boolean checkAuth(List<Integer> pathIdList, String uri);

    boolean update(CategoryRequest request, Integer id);

    List<Category> getChildVoListByPid(Integer pid);

    int checkName(String name, Integer type);

    boolean checkUrl(String uri);

    boolean updateStatus(Integer id);

    /**
     * 新增分类表
     */
    Boolean create(CategoryRequest categoryRequest);

    /**
     * 获取文章分类列表
     * @return List<Category>
     */
    List<Category> findArticleCategoryList();

    List<CategoryTreeVo> getListTreeByTyper(Integer type, Integer status, Integer pid);

    List<Answer> getListAnswer(Answer request, PageParamRequest pageParamRequest);
    int deleteAnswer(Integer id);
    Answer infoAnswer(Integer id);
    Boolean saveAnswer(Answer request);
    boolean updateAnswer(Answer request);

}
