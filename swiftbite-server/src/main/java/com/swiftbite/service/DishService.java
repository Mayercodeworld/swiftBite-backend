package com.swiftbite.service;

import com.swiftbite.dto.DishDTO;
import com.swiftbite.dto.DishPageQueryDTO;
import com.swiftbite.entity.Dish;
import com.swiftbite.result.PageResult;
import com.swiftbite.vo.DishVO;

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

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    List<DishVO> listWithFlavor(Dish dish);
}
