package com.laisd.springcloud.controller;

import com.laisd.springcloud.entities.CommonResult;
import com.laisd.springcloud.entities.Payment;
import com.laisd.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping(value = "/payment/create")
    public CommonResult create(@RequestBody Payment payment) {
        int result = paymentService.create(payment);

        log.info("****插入结果:" + result);

        if (result > 0) {
            return new CommonResult(200, "插入数据库成功, serverPort=" + serverPort, result);
        } else {
            return new CommonResult(444, "插入数据库失败", null);
        }
    }

    @GetMapping(value = "/payment/get/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);

        log.info("查询结果:" + payment);
        log.info("serverPort=" + serverPort);

        if (payment != null) {
            return new CommonResult(200, "查询成功, serverPort=" + serverPort, payment);
        } else {
            return new CommonResult(444, "查询失败", null);
        }
    }

    @GetMapping(value = "/payment/discovery")
    public Object discovery() {
        //方法一
        List<String> serviceList = discoveryClient.getServices();
        for (String element : serviceList) {
            log.info("------element:" + element);
        }
        //方法二  一个微服务下的全部服务名称
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : serviceInstances) {
            log.info(instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getPort() + "\t" + instance.getUri());
        }
        return this.discoveryClient;
    }
}