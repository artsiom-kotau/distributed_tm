package com.cs.distr.shared.api.transaction.manager;

import java.io.Serializable;

/**
 * @author: artsiom.kotov
 */
public class ChangeObject implements Change, Serializable {

    private static final long serialVersionUID = 1498619443915943469L;

    private Object tid;
    private String serviceId;
    private Object oldObject;
    private Object newObject;
    private OperationType operationType;

    public ChangeObject(Object tid, String serviceId, Object object, OperationType operationType) {
        if (tid == null || serviceId == null || object == null || operationType == null) {
            throw new IllegalArgumentException("Some arguments are null");
        }
        this.tid = tid;
        this.serviceId = serviceId;
        switch (operationType) {
            case CREATE: {
                newObject = object;
                break;
            }
            case DELETE: {
                oldObject = object;
                break;
            }
            default: {
                throw new IllegalArgumentException(operationType + ". Operation type is invalid");
            }

        }
    }

    public ChangeObject(Object tid, String serviceId, Object oldObject, Object newObject, OperationType operationType) {
        this.tid = tid;
        this.serviceId = serviceId;
        this.oldObject = oldObject;
        this.newObject = newObject;
        this.operationType = operationType;
    }

    public ChangeObject(Object tid, Object oldObject, Object newObject, OperationType operationType) {
        this.tid = tid;
        this.oldObject = oldObject;
        this.newObject = newObject;
        this.operationType = operationType;
    }

    @Override
    public Object getTid() {
        return tid;
    }

    @Override
    public Object getOldObject() {
        return oldObject;
    }

    @Override
    public Object getNewObject() {
        return newObject;
    }

    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }
}
