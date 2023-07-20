package com.csm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csm.reggie.common.R;
import com.csm.reggie.entity.Category;
import com.csm.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
