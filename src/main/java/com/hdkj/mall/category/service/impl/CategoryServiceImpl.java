package com.hdkj.mall.category.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.PageParamRequest;
import com.constants.CategoryConstants;
import com.constants.Constants;
import com.exception.MallException;
import com.github.pagehelper.PageHelper;
import com.hdkj.mall.article.dao.UserAttentionDao;
import com.hdkj.mall.article.service.DynamicService;
import com.hdkj.mall.category.model.Answer;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.category.dao.AnswerDao;
import com.hdkj.mall.category.dao.CategoryDao;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.service.CategoryService;
import com.hdkj.mall.user.model.User;
import com.utils.MallUtil;
import com.hdkj.mall.article.model.UserAttention;
import com.hdkj.mall.category.request.CategoryRequest;
import com.hdkj.mall.category.request.CategorySearchRequest;
import com.hdkj.mall.user.service.UserService;
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
 * CategoryServiceImpl 接口实现
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Resource
    private CategoryDao dao;

    @Resource
    private UserAttentionDao userAttentionDao;

    @Resource
    private AnswerDao answerDao;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    @Autowired
    private UserService userService;
    @Autowired
    private DynamicService dynamicService;

    /**
     * 获取分类下子类的数量
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return List<Category>
     */
    @Override
    public List<Category> getList(CategorySearchRequest request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getPid()){
            lambdaQueryWrapper.eq(Category::getPid, request.getPid());
        }
        if(null != request.getType()){
            lambdaQueryWrapper.eq(Category::getType, request.getType());
        }
        if(ObjectUtil.isNotNull(request.getStatus()) && request.getStatus() >= 0){
            lambdaQueryWrapper.eq(Category::getStatus, request.getStatus().equals(CategoryConstants.CATEGORY_STATUS_NORMAL));
        }
        if(null != request.getName()){
            lambdaQueryWrapper.like(Category::getName, request.getName());
        }
        lambdaQueryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 通过id集合获取列表
     * @param idList List<Integer> id集合
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return List<Category>
     */
    @Override
    public List<Category> getByIds(List<Integer> idList) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Category::getId, idList);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 通过id集合获取列表 id => name
     * @param cateIdList List<Integer> id集合
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return HashMap<Integer, String>
     */
    @Override
    public HashMap<Integer, String> getListInId(List<Integer> cateIdList) {
        HashMap<Integer, String> map = new HashMap<>();
        List<Category> list = getByIds(cateIdList);
        for (Category category : list){
            map.put(category.getId(), category.getName());
        }
        return map;
    }

    /**
     * 查询id和url是否存在
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return Boolean
     */
    @Override
    public Boolean checkAuth(List<Integer> pathIdList, String uri) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Category::getId, pathIdList).eq(Category::getUrl, uri);
        List<Category> categoryList = dao.selectList(lambdaQueryWrapper);
        if(categoryList.size() < 1){
            return false;
        }

        return true;
    }

    /**
     * 修改
     * @param request CategoryRequest
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return bool
     */
    @Override
    public boolean update(CategoryRequest request, Integer id) {
        try{
            //修改分类信息
            Category category = new Category();
            BeanUtils.copyProperties(request, category);
            category.setId(id);
            category.setPath(getPathByPId(category.getPid()));

            updateById(category);

            //如状态为关闭，那么所以子集的状态都关闭
            if(!request.getStatus()){
                updateStatusByPid(id, false);
            }else{
                //如是开启，则父类的状态为开启
                updatePidStatusById(id);
            }

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 开启父级状态
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    private void updatePidStatusById(Integer id) {
        Category category = getById(id);
        List<Integer> categoryIdList = MallUtil.stringToArrayByRegex(category.getPath(), "/");
        categoryIdList.removeIf(i -> i.equals(0));
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        if(categoryIdList.size() < 1){
            return;
        }
        for (Integer categoryId: categoryIdList) {
            Category categoryVo = new Category();
            categoryVo.setId(categoryId);
            categoryVo.setStatus(true);
            categoryArrayList.add(categoryVo);
        }
        updateBatchById(categoryArrayList);
    }

    /**
     * 获取分类下子类的数量
     * @param pid Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return bool
     */
    private int getChildCountByPid(Integer pid) {
        //查看是否有子类
        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/"+pid+"/");
        return dao.selectCount(objectQueryWrapper);
    }

    /**
     * 修改分类以及子类的状态
     * @param pid Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return bool
     */
    private int updateStatusByPid(Integer pid, boolean status) {
        //查看是否有子类
        Category category = new Category();
        category.setStatus(status);

        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.like("path", "/"+pid+"/");
        return dao.update(category, objectQueryWrapper);
    }

    @Override
    public String getPathByPId(Integer pid) {
        Category category = getById(pid);
        if(null != category){
            return category.getPath() + pid + "/";
        }
        return null;
    }

    /**
     * 带结构的无线级分类
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @Override
    public List<CategoryTreeVo> getListTree(Integer type, Integer status, String name) {
        return getTree(type, status,name,null);
    }

    /**
     * 带权限的属性结构
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @Override
    public List<CategoryTreeVo> getListTree(Integer type, Integer status, List<Integer> categoryIdList) {
        System.out.println("菜单列表:getListTree: type:" + type + "| status:" + status + "| categoryIdList:" + JSON.toJSONString(categoryIdList));
        return getTree(type, status,null,categoryIdList);
    }

    /**
     * 带结构的无线级分类
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    private List<CategoryTreeVo> getTree(Integer type, Integer status,String name, List<Integer> categoryIdList) {
        //循环数据，把数据对象变成带list结构的vo
        List<CategoryTreeVo> treeList = new ArrayList<>();

        LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(Category::getType, type);

        if(null != categoryIdList && categoryIdList.size() > 0){
            lambdaQueryWrapper.in(Category::getId, categoryIdList);
        }

        if(status >= 0){
            lambdaQueryWrapper.eq(Category::getStatus, status);
        }
        if(StringUtils.isNotBlank(name)){ // 根据名称模糊搜索
            lambdaQueryWrapper.like(Category::getName,name);
        }

        lambdaQueryWrapper.orderByDesc(Category::getSort);
        lambdaQueryWrapper.orderByAsc(Category::getId);
        List<Category> allTree = dao.selectList(lambdaQueryWrapper);
        if(allTree == null){
            return null;
        }
        // 根据名称搜索特殊处理 这里仅仅处理两层搜索后有子父级关系的数据
        if(StringUtils.isNotBlank(name) && allTree.size() >0){
            List<Category> searchCategory = new ArrayList<>();
            List<Integer> categoryIds = allTree.stream().map(Category::getId).collect(Collectors.toList());

            List<Integer> pidList = allTree.stream().filter(c -> c.getPid() > 0 && !categoryIds.contains(c.getPid()))
                    .map(Category::getPid).distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(pidList)) {
                pidList.forEach(pid -> {
                    searchCategory.add(dao.selectById(pid));
                });
            }
            allTree.addAll(searchCategory);
        }

        for (Category category: allTree) {
            CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
            BeanUtils.copyProperties(category, categoryTreeVo);
            treeList.add(categoryTreeVo);
        }


        //返回
        Map<Integer, CategoryTreeVo> map = new HashMap<>();
        //ID 为 key 存储到map 中
        for (CategoryTreeVo categoryTreeVo1 : treeList) {
            map.put(categoryTreeVo1.getId(), categoryTreeVo1);
        }

        List<CategoryTreeVo> list = new ArrayList<>();
        for (CategoryTreeVo tree : treeList) {
            //子集ID返回对象，有则添加。
            CategoryTreeVo tree1 = map.get(tree.getPid());
            if(tree1 != null){
                tree1.getChild().add(tree);
            }else {
                list.add(tree);
            }
        }
        System.out.println("无限极分类 : getTree:" + JSON.toJSONString(list));
        return list;
    }

    /**
     * 删除分类表
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return bool
     */
    @Override
    public int delete(Integer id) {
        //查看是否有子类, 物理删除
        if(getChildCountByPid(id) > 0){
            throw new MallException("当前分类下有子类，请先删除子类！");
        }

        return dao.deleteById(id);
    }

    /**
     * 获取分类下子类
     * @param pid Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return List<Category>
     */
    @Override
    public List<Category> getChildVoListByPid(Integer pid) {
        //查看是否有子类
        QueryWrapper<Category> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("status", CategoryConstants.CATEGORY_STATUS_NORMAL);
        objectQueryWrapper.like("path", "/"+pid+"/");
        return dao.selectList(objectQueryWrapper);
    }

    /**
     * 检测分类名称是否存在
     * @param name String 分类名
     * @param type int 类型
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return int
     */
    @Override
    public int checkName(String name, Integer type) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getName, name);
        if (ObjectUtil.isNotNull(type)) {
            lambdaQueryWrapper.eq(Category::getType, type);
        }
        return dao.selectCount(lambdaQueryWrapper);
    }

    /**
     * 检测url是否存在
     * @param uri String url
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return int
     */
    @Override
    public boolean checkUrl(String uri) {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Category::getUrl, uri);
        return dao.selectCount(lambdaQueryWrapper) > 0;
    }

    @Override
    public boolean updateStatus(Integer id) {
        Category category = getById(id);
        category.setStatus(!category.getStatus());
        return updateById(category);
    }

    /**
     * 新增分类
     * @param categoryRequest
     */
    @Override
    public Boolean create(CategoryRequest categoryRequest) {
        //检测标题是否存在
        if(checkName(categoryRequest.getName(), categoryRequest.getType()) > 0){
            throw new MallException("此分类已存在");
        }
        Category category = new Category();
        BeanUtils.copyProperties(categoryRequest, category);
        category.setPath(getPathByPId(category.getPid()));
        category.setExtra(systemAttachmentService.clearPrefix(category.getExtra()));
        category.setUrl(systemAttachmentService.clearPrefix(category.getUrl()));
        return save(category);
    }

    /**
     * 获取文章分类列表
     * @return List<Category>
     */
    @Override
    public List<Category> findArticleCategoryList() {
        LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.select(Category::getId, Category::getName);
        lambdaQueryWrapper.eq(Category::getType, Constants.CATEGORY_TYPE_ARTICLE);
        lambdaQueryWrapper.eq(Category::getStatus, true);
        lambdaQueryWrapper.orderByDesc(Category::getSort);
        lambdaQueryWrapper.orderByAsc(Category::getId);
        return dao.selectList(lambdaQueryWrapper);
    }
    /**
     * 带结构的无线级分类
     */
    @Override
    public List<CategoryTreeVo> getListTreeByTyper(Integer type, Integer status, Integer pid) {
        return getTreeByTyper(type, status,pid);
    }

    /**
     * 带结构的无线级分类
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    private List<CategoryTreeVo> getTreeByTyper(Integer type, Integer status,Integer pid) {

        LambdaQueryWrapper<Category> lambdaQuery = Wrappers.lambdaQuery();
        lambdaQuery.eq(Category::getType, type);
        if(status >= 0){
            lambdaQuery.eq(Category::getStatus, status);
        }
        if(pid!=null){
            lambdaQuery.eq(Category::getPid, pid);
        }
        List<Category> categoryList = dao.selectList(lambdaQuery);

        //循环数据，把数据对象变成带list结构的vo
        List<CategoryTreeVo> treeList = new ArrayList<>();
        if(categoryList!=null && categoryList.size()>0){
//            Category category1 = categoryList.get(0);
//            LambdaQueryWrapper<Category> lambdaQueryWrapper = Wrappers.lambdaQuery();
//            lambdaQueryWrapper.eq(Category::getType, type);
//            lambdaQueryWrapper.eq(Category::getPid, category1.getId());
//            if(status >= 0){
//                lambdaQueryWrapper.eq(Category::getStatus, status);
//            }
//            lambdaQueryWrapper.orderByDesc(Category::getSort);
//            lambdaQueryWrapper.orderByAsc(Category::getId);
//            List<Category> allTree = dao.selectList(lambdaQueryWrapper);
//            if(allTree == null){
//                return null;
//            }
//
//            for (Category category: allTree) {
//                CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
//                BeanUtils.copyProperties(category, categoryTreeVo);
//                treeList.add(categoryTreeVo);
//            }

            User user = userService.getInfo();
            for (Category category: categoryList) {
                CategoryTreeVo categoryTreeVo = new CategoryTreeVo();
                BeanUtils.copyProperties(category, categoryTreeVo);
                if(user ==null){
                    categoryTreeVo.setIsGz(false);
                }else{
                    try {
                        UserAttention userAttention = new UserAttention();
                        userAttention.setUid(user.getUid());
                        userAttention.setDid(categoryTreeVo.getId());
                        userAttention.setType(2);
                        categoryTreeVo.setIsGz(dynamicService.isUserAttention(userAttention));
                    }catch (Exception e){
                        categoryTreeVo.setIsGz(false);
                    }
                }
                try {
                    QueryWrapper queryWrapper = new QueryWrapper();
                    queryWrapper.eq("type", 2);
                    queryWrapper.eq("did", categoryTreeVo.getId());
                    categoryTreeVo.setRs(userAttentionDao.selectCount(queryWrapper));

                }catch (Exception e){
                    categoryTreeVo.setRs(0);
                }

                treeList.add(categoryTreeVo);
            }

            //返回
            System.out.println("无限极分类 : getTree:" + JSON.toJSONString(treeList));
            return treeList;
        }else{
            return null;
        }
    }


    /**
     * 获取分类下子类的数量
     * @param request 请求参数
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-04-16
     * @return List<Category>
     */
    @Override
    public List<Answer> getListAnswer(Answer request, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<Answer> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(null != request.getCid()){
            lambdaQueryWrapper.eq(Answer::getCid, request.getCid());
        }
        if(null != request.getTitle()){
            lambdaQueryWrapper.like(Answer::getTitle, request.getTitle());
        }
        lambdaQueryWrapper.orderByDesc(Answer::getSort).orderByAsc(Answer::getId);
        return answerDao.selectList(lambdaQueryWrapper);
    }

    /**
     * @param request
     */
    @Override
    public Boolean saveAnswer(Answer request) {
        if(answerDao.insert(request)>0){
            return true;
        }else{
            return false;
        }
    }
    @Override
    public boolean updateAnswer(Answer request) {
        try{
            if(answerDao.updateById(request)>0){
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public Answer infoAnswer(Integer id) {
        return answerDao.selectById(id);
    }
    @Override
    public int deleteAnswer(Integer id) {
        return answerDao.deleteById(id);
    }
}

