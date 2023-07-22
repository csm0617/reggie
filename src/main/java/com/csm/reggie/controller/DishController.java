package com.csm.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.csm.reggie.common.R;
import com.csm.reggie.dto.DishDto;
import com.csm.reggie.entity.Category;
import com.csm.reggie.entity.Dish;
import com.csm.reggie.entity.DishFlavor;
import com.csm.reggie.service.CategoryService;
import com.csm.reggie.service.DishFlavorService;
import com.csm.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    //注入业务需要的 service层
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping()
    public R<String> save(@RequestBody DishDto dishDto) {//DTO用于接收前端传过来的Json对象，因为业务需求大都和entity中的属性不一样，所以需要构造这样一个对象
        //日志输出接收到的参数
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        dishService.page(pageInfo, queryWrapper);
        //这里不能直
        //忽略records属性，将pageInfo的其他信息拷贝到dishDtoPage中，也就是只保留分页信息等，不保留分页的内容
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");

        // !!!不建议这种方式去做多表联查的操作，在循环里面写sql很不好，最好是直接用mybatis写多表联查的，
        //下面是为了将菜品的分类名称赋值到Dto中，因为dish表中只保存了categoryId
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> dishDtoRecords = records.stream().map(
                item -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item, dishDto);
                    Category category = categoryService.getById(item.getCategoryId());
                    //判空处理
                    if (category != null) {
                        String categoryName = category.getName();
                        dishDto.setCategoryName(categoryName);
                    }
                    return dishDto;
                }
        ).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoRecords);

        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询单个的菜品信息以及口味
     * @param id
     * @return
     */

    @GetMapping("/{id}")
    public R<DishDto> getDishDto(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
//        Dish dish = new Dish();
//        BeanUtils.copyProperties(dishDto,dish);
//        dishService.updateById(dish);
//        List<DishFlavor> flavors = dishDto.getFlavors();
//        dishFlavorService.updateBatchById(flavors);
//        return R.success("修改菜品信息成功");
        dishService.updateWithFlavor(dishDto);
        return R.success("修改菜品信息成功");
    }

//    @GetMapping("/list")
//    public  R<List<Dish>> list(Dish dish){
//        //日志输出入参
//        log.info(dish.toString());
//        //构造条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        //添加条件，查询状态为1（起售状态）的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId ,dish.getCategoryId()).orderByDesc(Dish::getSort);
//
//        List<Dish> dishList = dishService.list(queryWrapper);
//        return R.success(dishList);
//
//    }


    @GetMapping("/list")
    public  R<List<DishDto>> list(Dish dish){
        //日志输出入参
        log.info(dish.toString());
        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId ,dish.getCategoryId()).orderByDesc(Dish::getSort);
        List<Dish> dishList = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = dishList.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(dishId != null, DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(wrapper);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);

    }

}
