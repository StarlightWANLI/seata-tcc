package cn.tedu.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("cn.tedu.order.mapper")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"cn.tedu.order"})
@EnableFeignClients(basePackages = {"cn.tedu.order.feign"})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

}
