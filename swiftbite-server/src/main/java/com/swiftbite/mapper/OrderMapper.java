package com.swiftbite.mapper;

import com.github.pagehelper.Page;
import com.swiftbite.dto.GoodsSalesDTO;
import com.swiftbite.dto.OrdersPageQueryDTO;
import com.swiftbite.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /* 客户端 */
    /**
     * 用户下单
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     */
    @Select("select * from orders where number = #{outTradeNo} and user_id = #{currentId}")
    Orders getByNumberAndUserId(String outTradeNo, Long currentId);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);


    /**
     * 根据状态和下单时间查询超时订单
     * @return
     */
    @Select("select id from orders where status = #{status} and order_time < #{time};")
    List<Integer> getByStatusAndOrderTimeLT(Integer status, LocalDateTime time);


    /**
     * 批量删除所有超时订单
     * @param ids
     */
    void batchDelete(List<Integer> ids);

    /**
     * 分页条件查询并按下单时间排序
     * @param ordersPageQueryDTO
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据id查询订单
     * @param id
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);

    /**
     * 更新订单超时原因
     * @param ids
     */
    void updateStatus(@Param("ids")List<Integer> ids, @Param("order")Orders order);


    /**
     * 根据状态统计订单数量
     * @param status
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);

    /**
     * 根据动态条件统计营业额数据
     * @param map
     * @return
     */
    Double sumByMap(Map map);

    /**
     * 根据动态条件统计订单数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

    /**
     *  销量排名top10统计
     */
    List<GoodsSalesDTO> getSalesTop10ByMap(Map map);
}
