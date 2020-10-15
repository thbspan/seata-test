package org.test.seata.httpclient.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.test.seata.httpclient.dao.ProductDao;
import org.test.seata.httpclient.service.ProductService;


import io.seata.core.context.RootContext;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductDao productDao;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class) // 开启新事务
    @Override
    public void reduceStock(Long productId, Integer amount) throws Exception {
        LOGGER.info("[reduceStock] current XID: {}", RootContext.getXID());

        // 检查库存
        checkStock(productId, amount);
        LOGGER.info("[reduceStock] 开始扣减 {} 库存", productId);
        int updateCount = productDao.reduceStock(productId, amount);
        if (updateCount == 0) {
            LOGGER.warn("[reduceStock] 扣除 {} 库存失败", productId);
            throw new Exception("库存不足");
        }
        LOGGER.info("[reduceStock] 扣除 {} 库存成功", productId);
    }

    private void checkStock(Long productId, Integer requiredAmount) throws Exception {
        LOGGER.info("[checkStock] 检查 {} 库存", productId);
        Integer stock = productDao.getStock(productId);
        if (stock == null || stock < requiredAmount) {
            LOGGER.warn("[checkStock] {} 库存不足，当前库存: {}", productId, stock);
            throw new Exception("库存不足");
        }
    }
}
