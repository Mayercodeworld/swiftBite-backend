package com.swiftbite.mapper;

import com.swiftbite.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 新增菜品口味
     */
    void add(List<DishFlavor> flavors);

    /**
     * 根据dishID查询口味
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

    /**
     * 根据dishId删除flvors
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据dishIds批量删除flvors
     * @param dishIds
     */
    void deleteByDishIds(Long[] dishIds);
}
