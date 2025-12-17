package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeEditPasswordDto;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.vo.PageResultVO;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    void addEmp(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     */
    void editPassword(EmployeeEditPasswordDto employeeEditPasswordDto);

    /**
     * 根据id查询员工
     */
    Employee findByEmpId(Integer id);

    /**
     * 分页查询员工
     */
    PageResultVO<Employee> searchByPage(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 编辑员工
     */
    void editEmp(EmployeeDTO employeeDTO);

    /**
     * 启用、禁用员工账号
     */
    void editStatus(Integer status, Integer id);
}
