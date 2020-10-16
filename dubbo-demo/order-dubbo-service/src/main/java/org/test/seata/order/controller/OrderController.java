package org.test.seata.order.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.test.seata.order.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @DubboReference
    private OrderService orderService;

    @PostMapping("/create")
    public Integer createOrder(@RequestParam("userId") Long userId,
                               @RequestParam("productId") Long productId,
                               @RequestParam("price") Integer price) throws Exception {
        LOGGER.info("[createOrder] receive create order request, userId:{}, productId:{}, price:{}", userId, productId, price);
        return orderService.createOrder(userId, productId, price);
    }
}
