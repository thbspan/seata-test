package org.test.seata.httpclient.service;

/**
 * 账户 Service
 */
public interface AccountService {

    /**
     * 扣减余额
     *
     * @param userId 用户编号
     * @param price  扣减金额
     * @throws Exception 扣减失败抛出异常
     */
    void reduceBalance(Long userId, Integer price) throws Exception;
}
