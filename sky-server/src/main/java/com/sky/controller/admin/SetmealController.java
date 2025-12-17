package com.sky.controller.admin;

import com.sky.service.SetmealService;
import io.swagger.annotations.ApiModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@ApiModel(description = "套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

}
