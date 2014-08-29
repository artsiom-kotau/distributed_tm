package com.cs.distr.shared.api.transaction;

import javax.transaction.Transaction;

/**
 * @author: artsiom.kotov
 */
public interface DistributeTransaction extends Transaction {

    String getTid();

    void addTransactionNode(TransactionNode transaction);

    void setStatus(int status);

    void finalizeTransaction();
}
