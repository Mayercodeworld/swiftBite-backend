package com.swiftbite.dto;

import lombok.Data;

import java.io.Serializable;

// 接收数据的DTO不再考虑多余数据，其他数据可以在service层中通过对象拷贝来封装
@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    private String username;

    private String name;

    private String phone;

    private String sex;

    private String idNumber;
}