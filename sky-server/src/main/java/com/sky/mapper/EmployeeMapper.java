package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.annotation.Test;
import com.sky.dto.EmployeeEditPasswordDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 新增员工
     * @param employee
     */
    @Insert("INSERT INTO employee (name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES (#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(value = OperationType.INSERT)
    void addEmp(Employee employee);

    /**
     * 根据id查询员工
     * 自动封装为employee对象
     */
    @Select("select * from employee where id = #{id}")
    Employee findByEmpId(Integer id);

    /**
     * 修改密码
     */
    @Update("update employee set password = #{newPassword} where id = #{empId}")
    void editPassword(EmployeeEditPasswordDTO employeeEditPasswordDto);

    /**
     * 分页查询员工
     */

    List<Employee> searchByPage(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 编辑员工 or 启用或禁用员工（都是同一操作只不过修改的内容不同）
     */
    @AutoFill(value = OperationType.UPDATE)
    @Test(value = OperationType.UPDATE)
    void editEmp(Employee employee);

//    /**
//     * 启用、禁用员工账号
//     */
//    @Update("update employee set status = #{status}, update_time = #{updateTime} where id = #{id} and status != #{status}")
//    void editStatus(Integer status, Long id, LocalDateTime updateTime);
}
