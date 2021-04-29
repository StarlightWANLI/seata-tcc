package cn.tedu.storage.tcc;

import cn.tedu.storage.entity.Storage;
import cn.tedu.storage.mapper.StorageMapper;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class StorageTccActionImpl implements StorageTccAction {

    @Autowired
    private StorageMapper storageMapper;

    @Transactional
    @Override
    public boolean prepareDecreaseStorage(BusinessActionContext businessActionContext, Long productId, Integer count) {
        log.info("减少商品库存，第一阶段，锁定减少的库存量，productId="+productId+"， count="+count);
        String xid = RootContext.getXID();
        log.info(" Transaction xid: " + xid);
        Storage storage = storageMapper.selectById(productId);
        if (storage.getResidue()-count<0) {
            throw new RuntimeException("库存不足");
        }

        /*
        库存减掉count， 冻结库存增加count
         */
        storageMapper.updateFrozen(productId, storage.getResidue()-count, storage.getFrozen()+count);

        //模拟异常
        if (Math.random() < 0.9999) {
            throw new RuntimeException("模拟try阶段出现 异常");
        }

        //保存标识
        ResultHolder.setResult(getClass(), businessActionContext.getXid(), "p");
        return true;
    }

    @Transactional
    @Override
    public boolean commit(BusinessActionContext businessActionContext) {
        long productId = Long.parseLong(businessActionContext.getActionContext("productId").toString());
        int count = Integer.parseInt(businessActionContext.getActionContext("count").toString());
        log.info("减少商品库存，第二阶段提交，productId="+productId+"， count="+count);

        //防止重复提交
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }

        storageMapper.updateFrozenToUsed(productId, count);

        //删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }

    @Transactional
    @Override
    public boolean rollback(BusinessActionContext businessActionContext) {

        long productId = Long.parseLong(businessActionContext.getActionContext("productId").toString());
        int count = Integer.parseInt(businessActionContext.getActionContext("count").toString());
        log.info("减少商品库存，第二阶段，回滚，productId="+productId+"， count="+count);

        //防止重复提交
        if (ResultHolder.getResult(getClass(), businessActionContext.getXid()) == null) {
            return true;
        }

        storageMapper.updateFrozenToResidue(productId, count);

        //删除标识
        ResultHolder.removeResult(getClass(), businessActionContext.getXid());
        return true;
    }
}
