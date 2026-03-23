package com.swiftbite.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryDishVO implements Serializable {
    private int id; // 菜品id
    private String name; // 菜品名称
    private Double price; // 菜品价格
    private String image; // 菜品图片
    private String description; // 菜品描述
}
