package com.cs.distr.shared.api.transaction.store;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: artsiom.kotov
 */
public class TidCash {

    private static final Map<Long,Tid> tids = new ConcurrentHashMap<Long,Tid>();

    public static void addTid(Long threadId, Tid tid) {
        tids.put(threadId,tid);
    }

    public static boolean isTidExist(Long threadId) {
        return tids.containsKey(threadId);
    }

    public static Tid getTid(Long threadId) {
        return tids.get(threadId);
    }

    public static void kill(Tid targetTid) {
        Iterator<Map.Entry<Long,Tid>> entryIterator = tids.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Long,Tid> entry = entryIterator.next();
            if (entry.getValue().equals(targetTid)) {
                entry.getValue().kill();
            }
        }
    }

    public static void removeKeysByTid(Tid targetTid) {
        Iterator<Map.Entry<Long,Tid>> entryIterator = tids.entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Long,Tid> entry = entryIterator.next();
            if (entry.getValue().equals(targetTid)) {
                entryIterator.remove();
            }
        }
    }

}
