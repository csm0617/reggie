package com.csm.reggie.dto;

import com.csm.reggie.entity.Dish;
import com.csm.reggie.entity.DishFlavor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//注意DishDto 继承了 Dish
public class DishDto extends Dish {

    //注意DishDto 继承了 Dish!!!
    //注意DishDto 继承了 Dish!!!
    //注意DishDto 继承了 Dish!!!

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
