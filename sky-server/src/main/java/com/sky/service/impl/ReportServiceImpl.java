package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

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

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        // 从begin-end
        List<LocalDate> dateList = new ArrayList<>();

        // 1、日期计算
        dateList.add(begin);
        while(!begin.equals(end)) {
            // 日期计算，计算指定日期后一天，不能单纯的 + 1
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 存放每天订单总数
        List<Integer> orderCountList = new ArrayList<>();
        // 存放每天有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        // 2、遍历dateList集合，查询每天的有效订单数和订单总数
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            orderCountList.add(getOrderCount(beginTime, endTime, null));
            validOrderCountList.add(getOrderCount(beginTime, endTime, Orders.COMPLETED));
        }

        // 计算时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        // 计算时间区间内的订单总数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        // 计算订单完成率
        Double orderCompletionRate;
        if(totalOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
        } else {
            orderCompletionRate = 0.0;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCompletionRate(orderCompletionRate)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .build();
    }

    /**
     * 销量排名top10统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10Statistics(LocalDate begin, LocalDate end) {
        // 查询每一天销量top10排名
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        Map map = new HashMap();
        map.put("begin", beginTime);
        map.put("end", endTime);
        map.put("status", Orders.COMPLETED);

        List<GoodsSalesDTO> salesTop10List = orderMapper.getSalesTop10ByMap(map);
        List<String> nameList = salesTop10List.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        List<Integer> numberList = salesTop10List.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());

        /*
        // 需要重新new出对象，较为复杂，可以使用上面的stream流来处理集合元素
        List nameList = new ArrayList();
        List numberList = new ArrayList();
        salesTop10List.forEach(data -> {
            nameList.add(data.getName());
            numberList.add(data.getNumber());
        });*/
        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {
        Map map = new HashMap();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);
        return orderMapper.countByMap(map);
    }
}
