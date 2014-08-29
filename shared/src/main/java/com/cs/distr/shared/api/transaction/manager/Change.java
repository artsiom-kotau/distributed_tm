package com.cs.distr.shared.api.transaction.manager;

/**
 * @author: artsiom.kotov
 */
public interface Change {

    String getServiceId();

    Object getTid();

    Object getOldObject();

    Object getNewObject();

    OperationType getOperationType();
}
