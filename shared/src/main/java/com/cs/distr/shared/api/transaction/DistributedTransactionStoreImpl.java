package com.cs.distr.shared.api.transaction;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: artsiom.kotov
 */
public class DistributedTransactionStoreImpl implements DistributeTransactionStore {

    private static final Map<String, DistributeTransaction> transactions = new ConcurrentHashMap<String, DistributeTransaction>();

    @Override
    public DistributeTransaction createTransaction() {
        String tid = String.valueOf(UUID.randomUUID().getMostSignificantBits());
        DistributeTransaction transaction = new DistributedTransactionImpl(tid);
        transactions.put(tid,transaction);
        return transaction;
    }

    @Override
    public DistributeTransaction updateTransaction(DistributeTransaction transaction) {
        transactions.put(transaction.getTid(),transaction);
        return transaction;
    }

    @Override
    public DistributeTransaction findByTid(String tid) {
        return transactions.get(tid);
    }
}
