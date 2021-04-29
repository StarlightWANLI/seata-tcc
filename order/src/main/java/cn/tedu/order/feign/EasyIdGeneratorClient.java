package cn.tedu.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "EASY-ID-GENERATOR")
public interface EasyIdGeneratorClient {
    @GetMapping("/segment/ids/next_id")
    Long nextId(@RequestParam String businessType);
}
