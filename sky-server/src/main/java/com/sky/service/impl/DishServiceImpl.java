package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper flavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品
     */
    @Override
    @Transactional //事务注解
    public void add(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 1、向菜品表插入1条数据
        dishMapper.add(dish);
        Long dishId = dish.getId();

        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.forEach(dishFlavor -> {
            dishFlavor.setDishId(dishId);
        });

        if(flavors != null && flavors.size() > 0) {
            // 2、向口味表插入n条数据
            flavorMapper.add(flavors);
        }

    }

    /**
     * 分页查询菜品
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = (Page<DishVO>)dishMapper.page(dishPageQueryDTO);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询菜品
     */
    @Override
    public DishVO getById(Long id) {
        // 先查询dish表
        DishVO dishVO = dishMapper.getById(id);
        Long dishId = dishVO.getId();

        // 再根据dishId查询dish_flavor表
        List<DishFlavor> list =  flavorMapper.getByDishId(dishId);
        dishVO.setFlavors(list);
        return dishVO;
    }

    /**
     * 根据分类id查询菜品（应该查询所有菜品，而非仅返回在售的菜品）
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.getByCategoryId(categoryId);
        return dishes;
    }

    /**
     * 菜品起售、停售
     */
    @Override
    public void startOrEnd(Integer status, Integer id) {
        dishMapper.startOrEnd(status, id);
    }

    /**
     * 批量删除菜品
     */
    @Override
    @Transactional
    public void delete(Long[] ids) {
        if(ids.length == 0) {
            return ;
        }

        // 1、判断当前菜品能否删除
        // 起售中
        for (Long id : ids) {
            DishVO dishVO = dishMapper.getById(id); // for循环每次都要查询数据库
            if(dishVO.getStatus() == StatusConstant.ENABLE) {
                // 起售中不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

        }
        // 被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids); // 仅查询一次数据库
        if(setmealIds != null && setmealIds.size() > 0) {
            // 当前菜品被套餐关联了，无法删除
            throw  new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 2、删除菜品
        // 删除菜品表中的菜品数据 批量删除
        dishMapper.delete(ids);

        // 删除菜品关联的口味数据
        /*for (Long dishId : ids) {
            flavorMapper.deleteByDishId(dishId);
        }*/
        flavorMapper.deleteByDishIds(ids);

    }

    /**
     * 修改菜品
     */
    @Override
    @Transactional // 确保事务的同时发生
    public void update(DishDTO dishDTO) {
        // 现根据dishDto中的id把dishFlvor中的味道删除，然后根据flvors重新写入
        Long id = dishDTO.getId();

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        // 1、根据id删除dishFlvor
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        flavorMapper.deleteByDishId(id);
        // 2、重新写入到flvors中
        if(!dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(id);
            });
            flavorMapper.add(dishFlavors);
        }

    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = flavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
