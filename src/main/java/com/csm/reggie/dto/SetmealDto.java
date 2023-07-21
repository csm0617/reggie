package com.csm.reggie.dto;

import com.csm.reggie.entity.Setmeal;
import com.csm.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    //保存套餐的多个菜品
    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
