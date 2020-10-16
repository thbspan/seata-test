package org.test.seata.order.service.impl;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.test.seata.account.service.AccountService;
import org.test.seata.order.dao.OrderDao;
import org.test.seata.order.dto.OrderDTO;
import org.test.seata.order.service.OrderService;
import org.test.seata.product.service.ProductService;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderDao orderDao;

    @Reference
    private AccountService accountService;
    @Reference
    private ProductService productService;

    @GlobalTransactional
    @Override
    public Integer createOrder(Long userId, Long productId, Integer price) throws Exception {
        Integer amount = 1;// 购买数量，写死为1
        LOGGER.info("[createOrder] current XID: {}", RootContext.getXID());

        // 1.扣减库存
        productService.reduceStock(productId, amount);
        // 2.扣减余额
        accountService.reduceBalance(userId, price);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setUserId(userId);
        orderDTO.setProductId(productId);
        orderDTO.setPayAmount(amount * price);
        // 3.保存订单
        int status = orderDao.saveOrder(orderDTO);
        LOGGER.info("[createOrder] 保存订单: {}, 保存结果：{}", orderDTO.getId(), status);
        // 返回订单编号
        return orderDTO.getId();
    }
}
