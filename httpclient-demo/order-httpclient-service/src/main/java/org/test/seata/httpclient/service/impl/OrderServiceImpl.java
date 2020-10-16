package org.test.seata.httpclient.service.impl;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.seata.httpclient.dao.OrderDao;
import org.test.seata.httpclient.dto.OrderDTO;
import org.test.seata.httpclient.service.OrderService;

import com.alibaba.fastjson.JSONObject;

import io.seata.core.context.RootContext;
import io.seata.integration.http.DefaultHttpExecutor;
import io.seata.spring.annotation.GlobalTransactional;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);
    @Autowired
    private OrderDao orderDao;

    @GlobalTransactional
    @Override
    public Integer createOrder(Long userId, Long productId, Integer price) throws Exception {
        Integer amount = 1;// 购买数量，写死为1
        LOGGER.info("[createOrder] current XID: {}", RootContext.getXID());

        // 1.扣减库存
        reduceStock(productId, amount);
        // 2.扣减余额
        reduceBalance(userId, price);

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

    private void reduceStock(Long productId, Integer amount) throws IOException {
        // 请求参数
        JSONObject params = new JSONObject().fluentPut("productId", productId.toString())
                .fluentPut("amount", amount.toString());
        // 执行调用，这里为了方便测试，请求地址固定
        // DefaultHttpExecutor 是 Seata 封装，在使用个 HttpClient 发起 HTTP 调用时，将 Seata 全局事务 XID 通过 Header 传递
        HttpResponse response = DefaultHttpExecutor.getInstance().executePost("http://127.0.0.1:8081", "/product/reduce-stock",
                params, HttpResponse.class);
        boolean success = Boolean.parseBoolean(EntityUtils.toString(response.getEntity()));
        if (!success) {
            throw new RuntimeException("扣除库存失败");
        }
    }

    private void reduceBalance(Long userId, Integer price) throws IOException {
        // 请求参数
        JSONObject params = new JSONObject().fluentPut("userId", userId.toString())
                .fluentPut("price", price.toString());
        // 执行调用，这里为了方便测试，请求地址固定
        // DefaultHttpExecutor 是 Seata 封装，在使用个 HttpClient 发起 HTTP 调用时，将 Seata 全局事务 XID 通过 Header 传递
        HttpResponse response = DefaultHttpExecutor.getInstance().executePost("http://127.0.0.1:8082", "/account/reduce-balance",
                params, HttpResponse.class);
        boolean success = Boolean.parseBoolean(EntityUtils.toString(response.getEntity()));
        if (!success) {
            throw new RuntimeException("扣除余额失败");
        }
    }
}
