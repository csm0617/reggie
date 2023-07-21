package com.csm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csm.reggie.common.R;
import com.csm.reggie.dto.SetmealDto;
import com.csm.reggie.entity.Category;
import com.csm.reggie.entity.Setmeal;
import com.csm.reggie.service.CategoryService;
import com.csm.reggie.service.SetmealDishService;
import com.csm.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody  SetmealDto setmealDto){
        log.info("套餐信息为：{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 分页查询套餐信息
     * @param page
     * @param pageSize
     * @param name
     * @return
     */

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //构造分页对象
        Page<Setmeal> pageInfo= new Page<>();
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //构造条件查询器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //把分页查询到的结果封装到page对象中
        setmealService.page(pageInfo,queryWrapper);
        //除了records以外其他的属性都复制到需要返回的page对象上
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        //这一步是为了给分类名称复制，因为Setmeal中只保存了categoryId，在setmealDto才有categoryName
        List<SetmealDto> setmealDtoRecords= records.stream().map(setmeal -> {
            SetmealDto setmealDto = new SetmealDto();
            //把setmeal的其他属性拷贝到setmealDto中
            BeanUtils.copyProperties(setmeal, setmealDto);
            Long categoryId = setmeal.getCategoryId();
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            //设置分类名称
            setmealDto.setCategoryName(category.getName());
            return setmealDto;
        }).collect(Collectors.toList());
        //把records再设置给新的分页对象
        setmealDtoPage.setRecords(setmealDtoRecords);

        return R.success(setmealDtoPage);

    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        //打断点或者用日志输出看能不能收到传入的参数
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }

}
