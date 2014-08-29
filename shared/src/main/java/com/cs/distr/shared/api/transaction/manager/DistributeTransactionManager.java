package com.cs.distr.shared.api.transaction.manager;

import com.cs.distr.shared.api.remote.transactional.ITransactionalService;

import javax.transaction.TransactionManager;
import java.util.List;
import java.util.Map;

/**
 * @author: artsiom.kotov
 */
public interface DistributeTransactionManager extends TransactionManager {

    void storeTransactionInfo(Change change);

    void registerRemoteService(List<ServiceExport> services);
}
