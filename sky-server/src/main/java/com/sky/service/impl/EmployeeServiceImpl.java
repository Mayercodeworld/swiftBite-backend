package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeEditPasswordDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.service.EmployeeService;
import com.sky.vo.PageResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 进行md5加密处理，和数据库中的密码进行对比
        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        System.out.println(password);
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void addEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();

        // 对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        // 默认账号的状态，默认状态 1表示正常， 0 表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        // 设置密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes())); // 设置默认密码123456（加密）

        // AOP切面自动实现
        //// 设置时间
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());
        //// 设置创建人id和修改人id
        //employee.setCreateUser(BaseContext.getCurrentId());
        //employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.addEmp(employee);
    }

    /**
     * 修改密码
     */
    @Override
    public void editPassword(EmployeeEditPasswordDTO employeeEditPasswordDTO) {
        Employee employee = employeeMapper.findByEmpId(employeeEditPasswordDTO.getEmpId());
        if(employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        String oldPassword = DigestUtils.md5DigestAsHex(employeeEditPasswordDTO.getOldPassword().getBytes());

        if(!oldPassword.equals(employee.getPassword())) {
            // 新旧密码不匹配
            throw new PasswordErrorException(MessageConstant.PASSWORD_NOT_MASTER_FAILES);
        }
        // 加密新密码
        String newPassword = DigestUtils.md5DigestAsHex(employeeEditPasswordDTO.getNewPassword().getBytes());
        employeeMapper.editPassword(employeeEditPasswordDTO);
    }

    /**
     * 根据id查询员工
     */
    @Override
    public Employee findByEmpId(Integer id) {
        Employee employee = employeeMapper.findByEmpId(id);
        if(employee == null) {
            // 不存在该用户
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return employee;
    }

    /**
     * 分页查询员工
     */
    @Override
    public PageResultVO<Employee> searchByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        List<Employee> employeeList = employeeMapper.searchByPage(employeePageQueryDTO); // 不用传递参数
        Page<Employee> p = (Page<Employee>) employeeList;
        return new PageResultVO<Employee>(p.getTotal(), p.getResult());
    }

    /**
     * 编辑员工
     */
    @Override
    public void editEmp(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        //// 修改时间
        //employee.setUpdateTime(LocalDateTime.now());
        //// 设置最后修改此用户的id
        //employee.setUpdateUser(BaseContext.getCurrentId());

        employeeMapper.editEmp(employee);
    }

    /**
     * 启用、禁用员工账号
     */
    @Override
    public void editStatus(Integer status, Long id) {
        Employee employee = Employee.builder()
                        .status(status)
                        .id(id)
                        .build();
        
        employeeMapper.editEmp(employee);
    }
}
