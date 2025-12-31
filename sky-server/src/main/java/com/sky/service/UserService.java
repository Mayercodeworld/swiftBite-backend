package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserService {

    /**
     * 微信登录
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
