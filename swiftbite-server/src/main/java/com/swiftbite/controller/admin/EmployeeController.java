package com.swiftbite.controller.admin;

import com.swiftbite.constant.JwtClaimsConstant;
import com.swiftbite.dto.EmployeeDTO;
import com.swiftbite.dto.EmployeeEditPasswordDTO;
import com.swiftbite.dto.EmployeeLoginDTO;
import com.swiftbite.dto.EmployeePageQueryDTO;
import com.swiftbite.entity.Employee;
import com.swiftbite.properties.JwtProperties;
import com.swiftbite.result.Result;
import com.swiftbite.service.EmployeeService;
import com.swiftbite.utils.JwtUtil;
import com.swiftbite.vo.EmployeeLoginVO;
import com.swiftbite.vo.PageResultVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录") // swagger插件表明api的名称等
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation(value = "退出登录")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @return
     */
    @PostMapping()
    @ApiOperation(value = "新增员工")
    public Result<String> add(@RequestBody EmployeeDTO employeeDto) {
        log.info("添加员工{}", employeeDto);
        employeeService.addEmp(employeeDto);
        return Result.success();
    }

    /**
     * 分页查询员工
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询员工")
    public Result<PageResultVO<Employee>> searchByPage(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("分页查询员工{}", employeePageQueryDTO);
        PageResultVO<Employee> list = employeeService.searchByPage(employeePageQueryDTO);
        return Result.success(list);
    }

    /**
     * 根据id查询员工
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询员工")
    public Result<Employee> findByEmpId(@PathVariable Integer id) {
        log.info("根据id查询员工信息{}", id);
        Employee employee = employeeService.findByEmpId(id);
        employee.setPassword("******");
        return Result.success(employee);
    }

    /**
     * 修改密码
     */
    @PutMapping("/editPassword")
    @ApiOperation(value = "修改密码")
    public Result<String> editPassword(@RequestBody EmployeeEditPasswordDTO employeeEditPasswordDTO) {
        log.info("修改员工密码{}", employeeEditPasswordDTO);
        employeeService.editPassword(employeeEditPasswordDTO);
        return Result.success();
    }

    /**
     * 编辑员工
     */
    @PutMapping()
    @ApiOperation(value = "编辑员工")
    public Result<String> editEmp(@RequestBody EmployeeDTO employeeDTO) {
        log.info("修改员工信息{}", employeeDTO);
        employeeService.editEmp(employeeDTO);
        return Result.success();
    }

    /**
     * 启用、禁用员工账号
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "启用、禁用账号")
    public Result<String> editStatus(@PathVariable Integer status, Long id) {
        log.info("编辑员工状态:{},id:{}",status, id);
        employeeService.editStatus(status, id);
        return Result.success();
    }
}
