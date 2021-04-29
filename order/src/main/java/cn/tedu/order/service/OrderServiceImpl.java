package cn.tedu.order.service;

import cn.tedu.order.entity.Order;
import cn.tedu.order.feign.AccountClient;
import cn.tedu.order.feign.EasyIdGeneratorClient;
import cn.tedu.order.feign.StorageClient;
import cn.tedu.order.mapper.OrderMapper;
import cn.tedu.order.tcc.OrderTccAction;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    // @Autowired
    // private OrderMapper orderMapper;
    @Autowired
    EasyIdGeneratorClient easyIdGeneratorClient;
    @Autowired
    private AccountClient accountClient;
    @Autowired
    private StorageClient storageClient;

    @Autowired
    private OrderTccAction orderTccAction;

    @GlobalTransactional
    @Override
    public void create(Order order) {
        // 从全局唯一id发号器获得id
        Long orderId = easyIdGeneratorClient.nextId("order_business");
        order.setId(orderId);
        String xid = RootContext.getXID();
        log.info("New Transaction Begins: " + xid);
        // orderMapper.create(order);

        // 这里修改成调用 TCC 第一节端方法
        orderTccAction.prepareCreateOrder(
                null,
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getCount(),
                order.getMoney());

        // 修改库存
        storageClient.decrease(order.getProductId(), order.getCount());

        // 修改账户余额
        accountClient.decrease(order.getUserId(), order.getMoney());

    }
}