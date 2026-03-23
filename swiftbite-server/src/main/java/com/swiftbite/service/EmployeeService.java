package com.swiftbite.service;

import com.swiftbite.dto.EmployeeDTO;
import com.swiftbite.dto.EmployeeEditPasswordDTO;
import com.swiftbite.dto.EmployeeLoginDTO;
import com.swiftbite.dto.EmployeePageQueryDTO;
import com.swiftbite.entity.Employee;
import com.swiftbite.vo.PageResultVO;

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
    void editPassword(EmployeeEditPasswordDTO employeeEditPasswordDTO);

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
    void editStatus(Integer status, Long id);
}
