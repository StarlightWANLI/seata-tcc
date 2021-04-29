package cn.tedu.account.tcc;

import cn.tedu.account.entity.Account;
import cn.tedu.account.mapper.AccountMapper;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Slf4j
public class AccountTccActionImpl implements AccountTccAction {
    @Autowired
    private AccountMapper accountMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean prepareDecreaseAccount(BusinessActionContext businessActionContext, Long userId, BigDecimal money) {
        log.info("减少账户金额，第一阶段锁定金额，userId="+userId+"， money="+money);

        //剩余可用金额
        Account account = accountMapper.selectById(userId);
        if (account.getResidue().compareTo(money) < 0) {
            throw new RuntimeException("账户金额不足");
        }

        /*
         * 冻结可用金额
        余额-money
        冻结+money
         */
        accountMapper.updateFrozen(userId, account.getResidue().subtract(money), account.getFrozen().add(money));

        //模拟异常
        if (Math.random() < 0.9999) {
            throw new RuntimeException("模拟try阶段出现 异常");
        }

        //保存标识
        ResultHolder.setResult(getClass(), businessActionContext.getXid(), "p");
        return true;
    }

    /**
     * Confirm 方法一定要在 Try 方法之后执行。因此，Confirm 方法只需要关注重复提交的问题。
     * @param businessActionContext
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        long userId = Long.parseLong(businessActionContext.getActionContext("userId").toString());
        BigDecimal money =  new BigDecimal(businessActionContext.getActionContext("money").toString());
        log.info("减少账户金额，第二阶段，提交，userId="+userId+"， money="+money);

        //防止重复提交，确认try方法已经执行
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }

        accountMapper.updateFrozenToUsed(userId, money);

        //模拟异常
     /*   if (Math.random() < 0.9999) {
            throw new RuntimeException("模拟commit阶段出现 异常");
        }*/

        //删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {
        long userId = Long.parseLong(businessActionContext.getActionContext("userId").toString());
        BigDecimal money =  new BigDecimal(businessActionContext.getActionContext("money").toString());

        //防止重复提交，确保try方法已经执行，防止空回滚
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }

        log.info("减少账户金额，第二阶段，回滚，userId="+userId+"， money="+money);

        accountMapper.updateFrozenToResidue(userId, money);

        //模拟异常
  /*      if (Math.random() < 0.999) {
            throw new RuntimeException("模拟cancel 阶段出现 异常");
        }*/

        //删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }

}
