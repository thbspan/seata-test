package org.test.seata.httpclient.service;

public interface OrderService {

    Integer createOrder(Long userId, Long productId, Integer price) throws Exception;

}
