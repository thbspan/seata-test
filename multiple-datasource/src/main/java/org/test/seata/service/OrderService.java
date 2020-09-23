package org.test.seata.service;

public interface OrderService {
    /**
     * 创建订单
     */
    Integer createOrder(Long userId, Long productId, Integer price) throws Exception;
}
