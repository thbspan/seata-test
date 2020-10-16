package org.test.seata.product.service;

/**
 * 商品Service
 */
public interface ProductService {

    /**
     * 扣减库存
     *
     * @param productId 商品id
     * @param amount 扣减数量
     * @throws Exception 扣减失败异常
     */
    void reduceStock(Long productId, Integer amount) throws Exception;
}
