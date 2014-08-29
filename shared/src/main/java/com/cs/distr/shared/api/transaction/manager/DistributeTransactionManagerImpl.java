package com.cs.distr.shared.api.transaction.manager;

import com.cs.distr.shared.api.remote.ExtHttpInvokerProxyFactoryBean;
import com.cs.distr.shared.api.remote.transactional.ITransactionalService;
import com.cs.distr.shared.api.transaction.DistributeTransaction;
import com.cs.distr.shared.api.transaction.DistributeTransactionStore;
import com.cs.distr.shared.api.transaction.TransactionNode;
import com.cs.distr.shared.api.transaction.TransactionNodeImpl;
import com.cs.distr.shared.api.transaction.store.Tid;
import com.cs.distr.shared.api.transaction.store.TidCash;
import com.cs.distr.shared.api.transaction.store.TransactionCash;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import javax.transaction.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: artsiom.kotov
 */
public class DistributeTransactionManagerImpl implements DistributeTransactionManager, Serializable {

    private static final long serialVersionUID = 5434729655830763317L;

    private Map<String,HttpInvokerProxyFactoryBean> proxyServices;

    private DistributeTransactionStore distributeTransactionStore;

    public DistributeTransactionManagerImpl() {
        proxyServices = new ConcurrentHashMap<String, HttpInvokerProxyFactoryBean>();
    }

    public DistributeTransactionManagerImpl(DistributeTransactionStore distributeTransactionStore) {
        this.distributeTransactionStore = distributeTransactionStore;
        proxyServices = new ConcurrentHashMap<String, HttpInvokerProxyFactoryBean>();
    }

    public DistributeTransactionStore getDistributeTransactionStore() {
        return distributeTransactionStore;
    }

    public void setDistributeTransactionStore(DistributeTransactionStore distributeTransactionStore) {
        this.distributeTransactionStore = distributeTransactionStore;
    }

    @Override
    public void registerRemoteService(List<ServiceExport> exportServices) {
        for(ServiceExport serviceExport : exportServices) {

            HttpInvokerProxyFactoryBean invokerProxyFactoryBean = new ExtHttpInvokerProxyFactoryBean();
            invokerProxyFactoryBean.setServiceInterface(serviceExport.getServiceClass());
            invokerProxyFactoryBean.setServiceUrl(serviceExport.getServiceUrl());
            invokerProxyFactoryBean.afterPropertiesSet();

            if (proxyServices.containsKey(serviceExport.getServiceId())) {
                throw new IllegalArgumentException("Service with id " + serviceExport.getServiceId() +" have added already");
            }
            proxyServices.put(serviceExport.getServiceId(), invokerProxyFactoryBean);
        }
    }

    @Override
    public void storeTransactionInfo(Change change) {

        ITransactionalService transactionalService = getTransactionalService(change.getServiceId());
        if (transactionalService == null) {
            throw new IllegalStateException("Transactional service with id: "+change.getServiceId() + " is undefined");
        }

        TransactionNode node = new TransactionNodeImpl(change.getServiceId(), (Serializable)change.getOldObject(), (Serializable)change.getNewObject(),
                                                        change.getOperationType(), transactionalService);

        DistributeTransaction transaction = distributeTransactionStore.findByTid((String)change.getTid());

        if (transaction != null) {
            transaction.addTransactionNode(node);
            distributeTransactionStore.updateTransaction(transaction);
        }
    }

    @Override
    public void begin() throws NotSupportedException, SystemException {
        DistributeTransaction transaction = getTransactionIfExist();
        transaction.setStatus(Status.STATUS_ACTIVE);
        System.out.println("Transaction "+transaction.getTid()+" begin");
    }

    @Override
    public void commit()
            throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException,
            SystemException {
        DistributeTransaction transaction = getTransactionIfExist();
        if (transaction == null) {
            throw new IllegalStateException("No transaction");
        }
        transaction.commit();
        clearTransaction();
        System.out.println("Transaction "+transaction.getTid()+" commit");
    }

    @Override
    public int getStatus() throws SystemException {
        DistributeTransaction transaction = getDistributeTransaction();
        if (transaction == null) {
            throw new IllegalStateException("No transaction");
        }
        System.out.println("TM: getStatus()");
        return transaction.getStatus();
    }

    @Override
    public Transaction getTransaction() throws SystemException {
        System.out.println("TM: getTransaction()");
        return getDistributeTransaction();
    }

    @Override
    public void resume(Transaction tobj) throws InvalidTransactionException, IllegalStateException, SystemException {
        //todo
        /**
         *   взять транзакцию из стэка, ассоциированного с текщим tid
         */
    }

    @Override
    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        DistributeTransaction transaction = getTransactionIfExist();
        if (transaction == null) {
            throw new IllegalStateException("No transaction");
        }
        transaction.rollback();
        clearTransaction();
        System.out.println("Transaction "+transaction.getTid()+" rollback");
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        System.out.println("setRollbackOnly()");
    }

    @Override
    public void setTransactionTimeout(int seconds) throws SystemException {
        throw new SystemException("Operation not supported") ;
    }

    @Override
    public Transaction suspend() throws SystemException {
        //todo что-то должно происходить
        /**
         *  текущую транзакцию поместить в стэк
         *  создать новую транзакцию с таким же tid
         */
        return getTransactionIfExist();
    }

    protected DistributeTransaction getDistributeTransaction() {
        DistributeTransaction transaction;
        Tid tid =  TidCash.getTid(Thread.currentThread().getId());
        if (tid == null) {
            transaction  = distributeTransactionStore.createTransaction();
            tid = new Tid(transaction.getTid());
            TidCash.addTid(Thread.currentThread().getId(),tid);
            TransactionCash.addTransaction(tid,transaction);
        } else {
            transaction = TransactionCash.getTransaction(tid);
        }
        return transaction;
    }

    protected DistributeTransaction getTransactionIfExist() throws SystemException{
        DistributeTransaction transaction = null;
        Tid tid =  TidCash.getTid(Thread.currentThread().getId());
        if (tid != null) {
            if (TransactionCash.isTransactionExist(tid)) {
                transaction = TransactionCash.getTransaction(tid);
            } else {
                transaction = distributeTransactionStore.findByTid(tid.getUuid());
                if  (transaction != null) {
                    TransactionCash.addTransaction(tid,transaction);
                }
            }
        }
        return transaction;
    }

    private ITransactionalService getTransactionalService(String serviceId) {
        Object service = proxyServices.get(serviceId).getObject();
        return (ITransactionalService)service;
    }

    private void clearTransaction() throws SystemException{
        DistributeTransaction currentTransaction = getTransactionIfExist();
        if (currentTransaction != null) {
            currentTransaction.finalizeTransaction();
        }
        Tid tid = TidCash.getTid(Thread.currentThread().getId());
        if (tid != null) {
            TidCash.kill(tid);
            TransactionCash.removeTransaction(tid);
        }
    }
}
