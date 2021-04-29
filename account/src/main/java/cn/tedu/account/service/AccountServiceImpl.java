package cn.tedu.account.service;

import cn.tedu.account.tcc.AccountTccAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class AccountServiceImpl implements AccountService {
    // @Autowired
    // private AccountMapper accountMapper;

    @Autowired
    private AccountTccAction accountTccAction;

    @Override
    public void decrease(Long userId, BigDecimal money) {
        // accountMapper.decrease(userId,money);
        accountTccAction.prepareDecreaseAccount(null, userId, money);
    }
}