package cn.tedu.account.controller;

import cn.tedu.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@Slf4j
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/decrease")
    public String decrease(Long userId, BigDecimal money) {
        accountService.decrease(userId,money);
        return "用户账户扣减金额成功";
    }
}
