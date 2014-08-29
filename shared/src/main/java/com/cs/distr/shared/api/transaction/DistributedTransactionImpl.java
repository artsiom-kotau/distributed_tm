package com.cs.distr.shared.api.transaction;

import com.cs.distr.shared.api.transaction.manager.DistributeTransactionManager;

import javax.transaction.*;
import javax.transaction.xa.XAResource;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: artsiom.kotov
 */
public class DistributedTransactionImpl implements DistributeTransaction, Serializable {
    private static final long serialVersionUID = 5434729655830763317L;

    private String tid;
    private int status;
    private DistributeTransactionManager dtm;
    private List<TransactionNode> transactionNodes;

    public DistributedTransactionImpl(String tid) {
        this.tid = tid;
        this.status = Status.STATUS_NO_TRANSACTION;
        transactionNodes = new LinkedList<TransactionNode>();
    }

    @Override
    public String getTid() {
        return tid;
    }

    @Override
    public void addTransactionNode(TransactionNode transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction is null");
        }
        transactionNodes.add(transaction);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;

    }

    @Override
    public void commit()
            throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException,
            SystemException {

        this.status = Status.STATUS_COMMITTING;
        for(TransactionNode transaction : transactionNodes) {
            if (transaction.getStatus() < Status.STATUS_COMMITTED) {
                transaction.commit();
            } else {
                throw new IllegalStateException();
            }
            }
        this.status = Status.STATUS_COMMITTED;
    }

    @Override
    public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        throw new SystemException("Unsupported operation");
    }

    @Override
    public boolean enlistResource(XAResource xaRes) throws RollbackException, IllegalStateException, SystemException {
        throw new SystemException("Unsupported operation");
    }

    @Override
    public int getStatus() throws SystemException {
        return status;
    }

    @Override
    public void registerSynchronization(Synchronization sync) throws RollbackException, IllegalStateException, SystemException {
        throw new SystemException("Unsupported operation");
    }

    @Override
    public void rollback() throws IllegalStateException, SystemException {
        this.status = Status.STATUS_ROLLING_BACK;
        for(Transaction transaction : transactionNodes) {
             transaction.rollback();
        }
        this.status = Status.STATUS_ROLLEDBACK;
    }

    @Override
    public void setRollbackOnly() throws IllegalStateException, SystemException {
        throw new SystemException("Unsupported operation");
    }

    @Override
    public void finalizeTransaction() {
        transactionNodes.clear();
        this.status = Status.STATUS_NO_TRANSACTION;
    }
}
