package cn.tedu.storage.service;

import cn.tedu.storage.tcc.StorageTccAction;
import io.seata.core.context.RootContext;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {
    // @Autowired
    // private StorageMapper storageMapper;

    @Autowired
    private StorageTccAction storageTccAction;

    @Override
    public void decrease(Long productId, Integer count) throws Exception {
        String xid = RootContext.getXID();

        // storageMapper.decrease(productId,count);
        storageTccAction.prepareDecreaseStorage(null, productId, count);
    }

}
