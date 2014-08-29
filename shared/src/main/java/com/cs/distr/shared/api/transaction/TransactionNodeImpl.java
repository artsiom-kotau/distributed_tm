package com.cs.distr.shared.api.transaction;

import com.cs.distr.shared.api.remote.transactional.ITransactionalService;
import com.cs.distr.shared.api.transaction.manager.ChangeObject;
import com.cs.distr.shared.api.transaction.manager.OperationType;
import com.cs.distr.shared.api.transaction.store.Tid;
import com.cs.distr.shared.api.transaction.store.TidCash;

import javax.transaction.*;
import javax.transaction.xa.XAResource;
import java.io.Serializable;

/**
 * @author: artsiom.kotov
 */
public class TransactionNodeImpl implements TransactionNode {

    private String serviceId;
    private Serializable oldObject;
    private Serializable newObject;
    private OperationType type;
    private ITransactionalService transactionalService;
    private int status;

    public TransactionNodeImpl(String serviceId, Serializable oldObject, Serializable newObject, OperationType type, ITransactionalService transactionalService) {
        this.serviceId = serviceId;
        this.oldObject = oldObject;
        this.newObject = newObject;
        this.transactionalService = transactionalService;
        this.type = type;
        this.status = Status.STATUS_ACTIVE;
    }

    @Override
    public Serializable getOldObject() {
        return oldObject;
    }

    @Override
    public void setOldObject(Serializable obj) {
        this.status = Status.STATUS_ACTIVE;
        this.oldObject = obj;
    }

    @Override
    public Serializable getNewObject() {
        return newObject;
    }

    @Override
    public void setNewObject(Serializable obj) {
        this.status = Status.STATUS_ACTIVE;
        this.newObject = obj;
    }

    @Override
    public OperationType getOperationType() {
        return type;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    @Override
    public void commit()
            throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException,
            SystemException {
        status = Status.STATUS_COMMITTING;
        Tid tid = TidCash.getTid(Thread.currentThread().getId());
        this.transactionalService.commit(new ChangeObject(tid.getUuid(), oldObject, newObject, type));
        status = Status.STATUS_COMMITTED;
    }

    @Override
    public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        throw new SystemException("Unsupported");
    }

    @Override
    public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        throw new SystemException("Unsupported");
    }

    @Override
    public int getStatus() throws SystemException {
        return status;
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        throw new SystemException("Unsupported");
    }

    @Override
    public void rollback() throws IllegalStateException, SystemException {
        status = Status.STATUS_ROLLING_BACK;
        Tid tid = TidCash.getTid(Thread.currentThread().getId());
        this.transactionalService.rollback(new ChangeObject(tid.getUuid(),oldObject,newObject,type));
        status = Status.STATUS_ROLLEDBACK;
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
    }
}
