package org.test.seata.order.service;

public interface OrderService {
    Integer createOrder(Long userId, Long productId, Integer price) throws Exception;
}
