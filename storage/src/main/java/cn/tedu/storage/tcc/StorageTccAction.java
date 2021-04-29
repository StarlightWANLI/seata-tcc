package cn.tedu.storage.tcc;

import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface StorageTccAction {

    @TwoPhaseBusinessAction(name = "storageTccAction", commitMethod = "commit", rollbackMethod = "rollback")
    boolean prepareDecreaseStorage(BusinessActionContext businessActionContext,
                                   @BusinessActionContextParameter(paramName = "productId") Long productId,
                                   @BusinessActionContextParameter(paramName = "count") Integer count);

    boolean commit(BusinessActionContext businessActionContext);

    boolean rollback(BusinessActionContext businessActionContext);

}
