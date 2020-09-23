package org.test.seata.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.seata.dao.OrderDao;
import org.test.seata.dto.OrderDTO;
import org.test.seata.service.AccountService;
import org.test.seata.service.OrderService;
import org.test.seata.service.ProductService;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;

import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private AccountService accountService;

    @Autowired
    private ProductService productService;

    @DS("order-ds")
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
