package com.swiftbite.service;

import com.swiftbite.dto.UserLoginDTO;
import com.swiftbite.entity.User;

public interface UserService {

    /**
     * 微信登录
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
