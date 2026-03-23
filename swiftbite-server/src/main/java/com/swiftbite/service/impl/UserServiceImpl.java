package com.swiftbite.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.swiftbite.constant.MessageConstant;
import com.swiftbite.dto.UserLoginDTO;
import com.swiftbite.entity.User;
import com.swiftbite.exception.LoginFailedException;
import com.swiftbite.mapper.UserMapper;
import com.swiftbite.properties.WeChatProperties;
import com.swiftbite.service.UserService;
// import com.swiftbite.utils.HttpClientUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    // 微信服务url地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private RestClient restClient;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    /**
     * 微信登录
     */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 1、调用微信接口服务，获取当前微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

        // 2、判断openid是否为空
        if(openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 3、判断当前用户是否为新用户（查user表是否含有此openid 的用户）
        User user = userMapper.getByOpenId(openid);

        // 4、如果是新用户，自动完成注册
        if(user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        // 5、最后返回这个用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                WX_LOGIN,
                weChatProperties.getAppid(),
                weChatProperties.getSecret(),
                code);

        String result = restClient.get()
                .uri(url)
                .retrieve()
                .body(String.class);

        JSONObject jsonObject = JSON.parseObject(result);

        if (jsonObject.containsKey("errcode")) {
            throw new LoginFailedException("微信登录失败：" + jsonObject.getString("errmsg"));
        }

        return jsonObject.getString("openid");
    }
}
