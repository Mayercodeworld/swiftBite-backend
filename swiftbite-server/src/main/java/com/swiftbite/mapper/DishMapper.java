package com.swiftbite.mapper;

import com.swiftbite.annotation.AutoFill;
import com.swiftbite.dto.DishPageQueryDTO;
import com.swiftbite.entity.Dish;
import com.swiftbite.enumeration.OperationType;
import com.swiftbite.vo.AiQueryDishVO;
import com.swiftbite.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 新增菜品
     */

    @AutoFill(value = OperationType.INSERT)
    void add(Dish dish);

    /**
     * 分页查询菜品
     */
    List<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     */
    @Select("select * from dish where id = #{id}")
    DishVO getById(Long id);

    /**
     * 根据分类id查询菜品
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> getByCategoryId(Long categoryId);

    /**
     * 菜品起售、停售
     */
    @Update("update dish set status = #{status} where id = #{id}")
    void startOrEnd(Integer status, Integer id);

    /**
     * 批量删除菜品
     */
    void delete(Long[] ids);

    /**
     * 修改菜品
     */
    @AutoFill(value = OperationType.UPDATE)
    @Update("update dish set category_id = #{categoryId}, description = #{description}" +
            ", image = #{image}, name = #{name}, price = #{price}, update_time = #{updateTime}, update_user = #{updateUser} " +
            "where id = #{id}")
    void update(Dish dish);

    /**
     * 客户端根据分类Id查询菜品
     * @param dish
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId} and status = #{status}")
    List<Dish> list(Dish dish);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    @Select("select a.* from dish a left join setmeal_dish b on a.id = b.dish_id where b.setmeal_id = #{setmealId}")
    List<Dish> getBySetmealId(Long setmealId);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     * 获取起售中的菜品
     * @param status
     * @return
     */
    @Select("select id, name, price, image, description from dish where status = #{status}")
    List<AiQueryDishVO> aiQueyDish(Integer status);
}
