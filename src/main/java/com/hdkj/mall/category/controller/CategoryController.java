package com.hdkj.mall.category.controller;

import com.common.CommonPage;
import com.common.CommonResult;
import com.common.PageParamRequest;
import com.exception.MallException;
import com.hdkj.mall.category.model.Answer;
import com.hdkj.mall.category.vo.CategoryTreeVo;
import com.hdkj.mall.system.service.SystemAttachmentService;
import com.hdkj.mall.category.model.Category;
import com.hdkj.mall.category.request.CategoryRequest;
import com.hdkj.mall.category.service.CategoryService;
import com.utils.MallUtil;
import com.hdkj.mall.category.request.CategorySearchRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类表 前端控制器

 */
@Slf4j
@RestController
@RequestMapping("api/admin/category")
@Api(tags = "分类服务")

    public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SystemAttachmentService systemAttachmentService;

    /**
     * 分页显示分类表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public CommonResult<CommonPage<Category>>  getList(@ModelAttribute CategorySearchRequest request, @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<Category> categoryCommonPage = CommonPage.restPage(categoryService.getList(request, pageParamRequest));
        return CommonResult.success(categoryCommonPage);
    }

    /**
     * 新增分类表
     * @param categoryRequest 新增参数
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public CommonResult<String> save(@Validated CategoryRequest categoryRequest){
        if(categoryService.create(categoryRequest)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除分类表
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="分类ID")
    public CommonResult<String> delete(@RequestParam(value = "id") Integer id){
        if(categoryService.delete(id) > 0){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改分类表
     * @param id integer id
     * @param categoryRequest 修改参数
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ApiImplicitParam(name="id", value="分类ID")
    public CommonResult<String> update(@RequestParam Integer id, @ModelAttribute CategoryRequest categoryRequest){
        if(null == id || id <= 0) throw new MallException("id 参数不合法");
        categoryRequest.setExtra(systemAttachmentService.clearPrefix(categoryRequest.getExtra()));
        categoryRequest.setUrl(systemAttachmentService.clearPrefix(categoryRequest.getUrl()));
        if(categoryService.update(categoryRequest, id)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询分类表信息
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="分类ID")
    public CommonResult<Category> info(@RequestParam(value = "id") Integer id){
        Category category = categoryService.getById(id);
        return CommonResult.success(category);
    }


    /**
     * 查询分类表信息
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "获取tree结构的列表")
    @RequestMapping(value = "/list/tree", method = RequestMethod.GET)
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
     * 根据分类id集合获取分类数据
     * @param ids String id集合字符串
     * @since 2022-04-16
     */
    @ApiOperation(value = "根据id集合获取分类列表")
    @RequestMapping(value = "/list/ids", method = RequestMethod.GET)
    @ApiImplicitParam(name = "ids", value="分类id集合")
    public CommonResult<List<Category>> getByIds(@Validated @RequestParam(name = "ids") String ids){
        return CommonResult.success(categoryService.getByIds(MallUtil.stringToArray(ids)));
    }

    /**
     * 更改分类状态
     * @param id Integer 分类id
     * @since 2022-04-16
     * @return
     */
    @ApiOperation(value = "更改分类状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.GET)
    @ApiImplicitParam(name = "id", value="分类id")
    public CommonResult<Object> getByIds(@Validated @PathVariable(name = "id") Integer id){
        if (categoryService.updateStatus(id)) {
            return CommonResult.success("修改成功");
        } else {
            return CommonResult.failed("修改失败");
        }
    }



    /**
     * 分页显示分类表
     * @param request 搜索条件
     * @param pageParamRequest 分页参数
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "分页列表")
    @RequestMapping(value = "/listAnswer", method = RequestMethod.GET)
    public CommonResult<CommonPage<Answer>>  getListAnswer(@ModelAttribute Answer request, @ModelAttribute PageParamRequest pageParamRequest){
        CommonPage<Answer> categoryCommonPage = CommonPage.restPage(categoryService.getListAnswer(request, pageParamRequest));
        return CommonResult.success(categoryCommonPage);
    }

    /**
     * 新增分类表
     * @param request 新增参数
     */
    @ApiOperation(value = "新增")
    @RequestMapping(value = "/saveAnswer", method = RequestMethod.POST)
    public CommonResult<String> saveAnswer(@Validated Answer request){
        if(categoryService.saveAnswer(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 删除分类表
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "删除")
    @RequestMapping(value = "/deleteAnswer", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="分类ID")
    public CommonResult<String> deleteAnswer(@RequestParam(value = "id") Integer id){
        if(categoryService.deleteAnswer(id) > 0){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 修改分类表
     * @param request 修改参数
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "修改")
    @RequestMapping(value = "/updateAnswer", method = RequestMethod.POST)
    public CommonResult<String> updateAnswer(@ModelAttribute Answer request){
        if(null == request.getId() || request.getId() <= 0) throw new MallException("id 参数不合法");
        if(categoryService.updateAnswer(request)){
            return CommonResult.success();
        }else{
            return CommonResult.failed();
        }
    }

    /**
     * 查询分类表信息
     * @param id Integer
     * @author Mr.Zhou
     * @since 2022-04-16
     */
    @ApiOperation(value = "详情")
    @RequestMapping(value = "/infoAnswer", method = RequestMethod.GET)
    @ApiImplicitParam(name="id", value="分类ID")
    public CommonResult<Answer> infoAnswer(@RequestParam(value = "id") Integer id){
        return CommonResult.success( categoryService.infoAnswer(id));
    }
}



