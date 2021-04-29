package cn.tedu.order.mapper;

import cn.tedu.order.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;


public interface OrderMapper extends BaseMapper {
    void create(Order order);
    void updateStatus(@Param("orderId") Long orderId, @Param("status") Integer status);
}
