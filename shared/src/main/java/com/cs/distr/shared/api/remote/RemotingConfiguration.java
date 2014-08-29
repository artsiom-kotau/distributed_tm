package com.cs.distr.shared.api.remote;

import com.cs.distr.shared.api.remote.transactional.ITransactionalService;
import com.cs.distr.shared.api.transaction.manager.DistributeTransactionManager;
import com.cs.distr.shared.api.transaction.manager.ServiceExport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: artsiom.kotov
 */
public class RemotingConfiguration {

    private DistributeTransactionManager transactionManager;
    private Map<ITransactionalService, String> transactionalServices;

    public RemotingConfiguration(DistributeTransactionManager transactionManager, Map<ITransactionalService, String> transactionalServices) {
        this.transactionManager = transactionManager;
        this.transactionalServices = transactionalServices;
        init();
    }

    private void init() {
        List<ServiceExport> serviceExportList = new ArrayList<ServiceExport>();
        for(Map.Entry<ITransactionalService, String> entry : transactionalServices.entrySet()) {
            ITransactionalService transactionalService = entry.getKey();
            String serviceUrl = entry.getValue();
            String serviceId = transactionalService.getServiceId();
            Class serviceClass = ITransactionalService.class;
            serviceExportList.add(new ServiceExport(serviceId,serviceUrl,serviceClass));
        }
        transactionManager.registerRemoteService(serviceExportList);
    }
}
