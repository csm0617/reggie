package com.csm.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csm.reggie.dto.DishDto;
import com.csm.reggie.entity.Dish;
import com.csm.reggie.entity.DishFlavor;
import com.csm.reggie.mapper.DishMapper;
import com.csm.reggie.service.DishFlavorService;
import com.csm.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保留对应的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional  //重点，多表操作记得加事务，并在启动类开启事务注解 @EnableTransactionManagement
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish中
        //因为DishDto extends Dish所以可以直接调用service中的save方法进行保存，多余的属性会被舍弃
        this.save(dishDto);
        //save后dish的id通过mybatisPlus的雪花算法自动生成并入库了,此时获取id就有数据了，没有save之前的id可能是空
        Long id = dishDto.getId();
        //flavors中的逻辑外键dishId作绑定
        List<DishFlavor> flavors = dishDto.getFlavors();
        //重点，记得复习java8的stream流，函数式接口
        flavors = flavors.stream().map(
                flavor -> {
                    flavor.setDishId(id);
                    return flavor;
                }

        ).collect(Collectors.toList());


        //再将flavors入库,注意flavors是一个List,所以要批量插入saveBatch
        //保存菜品口味到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品以及口味
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品的基本信息，从dish表中查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表里查询
        //dish_flavor和dish表是多对一的关系
        //1.构造条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        //2.构造查询条件.eq
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        //3.调用dishFlavorService.list()
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        //4.把查出来的属性设置并返回
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional//多表操作加入事务注解，保证数据的一致性
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表，多余的字段会被舍弃
        this.updateById(dishDto);
        //清理当前菜品对应的口味数据---------dishFlavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据---------dishFlavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(flavor -> {
            flavor.setDishId(dishDto.getId());
            return flavor;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

}
