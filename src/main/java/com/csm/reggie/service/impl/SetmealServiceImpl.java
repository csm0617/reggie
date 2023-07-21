package com.csm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csm.reggie.common.CustomException;
import com.csm.reggie.dto.SetmealDto;
import com.csm.reggie.entity.Setmeal;
import com.csm.reggie.entity.SetmealDish;
import com.csm.reggie.mapper.SetmealMapper;
import com.csm.reggie.service.SetmealDishService;
import com.csm.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存菜单和菜品的关联关系
     *
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐的基本信息，操作setmeal,执行insert操作,save方法中要求传入Setmeal类型，
        // 但是SetmealDto继承了Setmeal,所以Setemeal属性与数据库字段的关系SetmealDto也有对应的，所以save方法可以传入setmealDto类型
        this.save(setmealDto);

        //保存套餐和菜品的关联信息，操作setmeal_dish，执行insert操作

        Long id = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(setmealDish -> {
            setmealDish.setSetmealId(id);
            return setmealDish;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐,同时需要删除套餐和菜品相关的数据，要求在售的套餐不能删除
     *
     * @param ids
     */

    @Transactional //多表操作事务注解
    @Override
    public void removeWithDish(List<Long> ids) {
        //先查询套餐的状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

//        虽然mybatisPlus很方便，但是本质的还是查询的sql，如果sql不知道，那么拼装条件构造器也不知道怎么装
//        select count(*) from setmeal where id in (1,2,3) and status =1 ;


        queryWrapper.in(ids != null, Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        //如果不能删除，抛出一个业务异常

        //统计一下选中的不可以删除的个数
        int count = this.count(queryWrapper);
        if (count > 0) {//说明有套餐在售
            //抛出业务异常
            List<Setmeal> setmealList = this.list(queryWrapper);
            List<String> names = setmealList.stream().map(
                    Setmeal::getName
            ).collect(Collectors.toList());
            String msg = names + ": 套餐正在售卖中，不能删除";
            //后台打印业务异常日志
            log.info(msg);
            throw new CustomException(msg);
        }


        //如果可以删除删除顺序，应该先删除关系表也就删除包含外键的表,套餐表中的id是关系表中的逻辑外键

        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(ids != null, SetmealDish::getSetmealId, ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

        //再删除套餐表中的数据
        this.removeByIds(ids);

    }

    //不完善的地方
    //补充开发功能，菜品停售对应的套餐也要停售，套餐起售时菜品也要起售。套餐删除要删除套餐菜品表。菜品删除要删除套餐菜品表，口味表，菜品表。
    //补充开发功能，套餐的起售，停售以及批量操作。
    //删除菜品或者修改菜品时，原有的菜品图片记得删除
}
