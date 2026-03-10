package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class
ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 从begin-end
        List<LocalDate> dateList = new ArrayList<>();

        // 1、日期计算
        dateList.add(begin);
        while(!begin.equals(end)) {
            // 日期计算，计算指定日期后一天，不能单纯的 + 1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 2、日期对应的营业额
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            // 查询date日期对应的营业额数据，营业额：状态“已完成”的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            // select sum(amount) from orders where order_time > ? and order_time < ? and status = 5;
            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
         }

        // 3、封装返回结果
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();

    }

    /**
     * 用户数据统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 从begin-end
        List<LocalDate> dateList = new ArrayList<>();

        LocalDate lastDate = begin.plusDays(-1);
        // 1、日期计算
        dateList.add(begin);
        while(!begin.equals(end)) {
            // 日期计算，计算指定日期后一天，不能单纯的 + 1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 每天新增用户数量
        List<Long> newUserList = new ArrayList<>();
        // 每天总用户数量
        List<Long> totalUserList = new ArrayList<>();

        // 计算begin前一天的用户总量
        Long lastTotalUser = userMapper.countTotalUserByTime(LocalDateTime.of(lastDate, LocalTime.MAX));
        for (LocalDate date : dateList) {
            // 2、计算当天的所有用户数量
            // select count(*) from user where create_time <= endTime;
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Long totalUser = userMapper.countTotalUserByTime(endTime);
            // 3、更新列表
            newUserList.add(totalUser - lastTotalUser);
            totalUserList.add(totalUser);

            // 4、更新上一天的用户总数
            lastTotalUser = totalUser;
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .totalUserList(StringUtils.join(totalUserList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .build();
    }


}
