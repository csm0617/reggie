package com.csm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csm.reggie.common.R;
import com.csm.reggie.entity.Category;
import com.csm.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category: {}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
        //创建分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //创建条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件条件，按sort字段进行排序
        queryWrapper.orderByAsc(Category::getSort);
        //调用service的page方法，将pageInfo和queryWrapper传进去，pageInfo会在service中的完成赋值
        categoryService.page(pageInfo,queryWrapper);
        //返回pageInfo
        return R.success(pageInfo);
    }

    /**
     * 根据id删除分类信息，如果菜品或者套餐绑定了该分类则不允许删除
     * @param ids
     * @return
     */

    @DeleteMapping()
    public R<String> delete(Long ids){
        //日志输出入参
        log.info("删除分类，id为： {}",ids);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    @PutMapping()
    public R<String> update(@RequestBody Category category){
        //输出入参
        log.info("删除的分类为：{}",category.toString());
        categoryService.updateById(category);
        return R.success("分类修改成功");
    }

    /**
     * 根据条件查询分类，菜品时下拉框获取到一个分类的list
     * @param category
     * @return
     */
    @GetMapping("/list")
    //这里前端没有传json对象，所以不能加@RequesBody注解
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件,根据Category表的sort字段进行排序,Asc升序，Desc降序
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //调用categoryService.list()来查询集合
        List<Category> list = categoryService.list(queryWrapper);
        //封装返回
        return R.success(list);
    }

}
