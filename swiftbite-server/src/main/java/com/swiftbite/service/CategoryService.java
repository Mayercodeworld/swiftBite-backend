package com.swiftbite.service;

import com.swiftbite.dto.CategoryDTO;
import com.swiftbite.dto.CategoryPageQueryDTO;
import com.swiftbite.entity.Category;
import com.swiftbite.result.PageResult;

import java.util.List;

public interface CategoryService {
    /**
     * 更新分类
     */
    void update(CategoryDTO categoryDTO);

    /**
     * 分类分页查询
     */
    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 启用、禁用分类
     */
    void startOrNot(Integer status, Long id);

    /**
     * 新增分类
     */
    void add(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     */
    void delete(Long id);

    /**
     * 根据类型查询分类
     */
    List<Category> list(Integer type);
}
