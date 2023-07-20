package com.csm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csm.reggie.common.CustomException;
import com.csm.reggie.entity.Category;
import com.csm.reggie.entity.Dish;
import com.csm.reggie.entity.Setmeal;
import com.csm.reggie.mapper.CategoryMapper;
import com.csm.reggie.service.CategoryService;
import com.csm.reggie.service.DishService;
import com.csm.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    //
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前先进行判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {

        //添加查询条件，根据分类id进行查询
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);

        int count1 = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if (count1 > 0) {
            //已经关联了菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //添加查询条件，根据分类id进行查询
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);

        int count2 = setmealService.count(setmealLambdaQueryWrapper);

        if (count2 > 0) {
            //已经关联了菜品，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        super.removeById(id);

    }
}
