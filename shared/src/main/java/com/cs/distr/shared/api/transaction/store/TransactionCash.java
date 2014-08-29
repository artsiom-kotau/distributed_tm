package com.cs.distr.shared.api.transaction.store;

import com.cs.distr.shared.api.transaction.DistributeTransaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: artsiom.kotov
 */
public class TransactionCash {
    private static final Map<Tid,DistributeTransaction> transactions = new ConcurrentHashMap<Tid,DistributeTransaction>();

    public static void addTransaction(Tid tid, DistributeTransaction transaction) {
        transactions.put(tid,transaction);
    }

    public static boolean isTransactionExist(Tid tid) {
        return transactions.containsKey(tid);
    }

    public static DistributeTransaction getTransaction(Tid tid) {
        return transactions.get(tid);
    }

    public static void removeTransaction(Tid tid) {
        if (transactions.containsKey(tid)) {
            transactions.remove(tid);
        }
    }
}
