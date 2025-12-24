package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import io.swagger.models.auth.In;

import java.util.ArrayList;
import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     */
    void add(DishDTO dishDTO);

    /**
     * 分页查询菜品
     */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     */
    DishVO getById(Long id);

    /**
     * 根据分类id查询菜品
     */
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 菜品起售、停售
     */
    void startOrEnd(Integer status, Integer id);

    /**
     * 批量删除菜品
     */
    void delete(Long[] ids);

    /**
     * 修改菜品
     */
    void update(DishDTO dishDTO);
}
