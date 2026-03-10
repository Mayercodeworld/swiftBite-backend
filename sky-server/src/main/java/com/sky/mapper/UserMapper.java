package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    /**
     * 微信登录
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 创建用户
     */
    void insert(User user);

    /**
     * 根据id查找用户
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据时间计算用户总数
     * @param endTime
     * @return
     */
    @Select("select count(*) from user where create_time <= #{endTime}")
    Long countTotalUserByTime(LocalDateTime endTime);
}
