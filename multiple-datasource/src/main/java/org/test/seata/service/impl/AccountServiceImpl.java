package org.test.seata.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.test.seata.dao.AccountDao;
import org.test.seata.service.AccountService;

import com.baomidou.dynamic.datasource.annotation.DS;

import io.seata.core.context.RootContext;

@Service
@DS(value = "amount-ds")
public class AccountServiceImpl implements AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountDao accountDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class) // 开启新事务
    @Override
    public void reduceBalance(Long userId, Integer price) throws Exception {
        LOGGER.info("[reduceBalance] current XID: {}", RootContext.getXID());

        checkBalance(userId, price);
        int updateCount = accountDao.reduceBalance(userId, price);
        if (updateCount == 0) {
            LOGGER.warn("[reduceBalance] 扣除用户 {} 余额失败", userId);
            throw new Exception("余额不足");
        }
        LOGGER.info("[reduceBalance] 扣除用户 {} 余额成功", userId);
    }

    private void checkBalance(Long userId, Integer price) throws Exception {
        LOGGER.info("[checkBalance] 检查用户 {} 余额", userId);
        Integer balance = accountDao.getBalance(userId);
        if (balance < price) {
            LOGGER.warn("[checkBalance] 用户 {} 余额不足，当前余额:{}", userId, balance);
            throw new Exception("余额不足");
        }
    }
}
