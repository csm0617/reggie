package com.csm.reggie.dto;

import com.csm.reggie.entity.Setmeal;
import com.csm.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
