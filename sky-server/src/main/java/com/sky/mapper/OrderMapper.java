package com.sky.mapper;

import com.sky.entity.Orders;
import com.sky.vo.OrderTimeoutVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {

    /**
     * C端：用户下单
     */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

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
     * 更新订单超时原因
     * @param ids
     */
    void updateStatus(@Param("ids")List<Integer> ids, @Param("order")Orders order);
}
