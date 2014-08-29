package com.cs.distr.shared.api.transaction;

/**
 * @author: artsiom.kotov
 */
public interface DistributeTransactionStore {

    DistributeTransaction createTransaction();

    DistributeTransaction updateTransaction(DistributeTransaction transaction);

    DistributeTransaction findByTid(String tid);
}
