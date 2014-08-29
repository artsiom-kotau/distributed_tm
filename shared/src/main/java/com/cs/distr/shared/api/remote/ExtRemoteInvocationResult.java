package com.cs.distr.shared.api.remote;

import org.springframework.remoting.support.RemoteInvocationResult;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: artsiom.kotov
 */
public class ExtRemoteInvocationResult extends RemoteInvocationResult {

    private static final long serialVersionUID = 3520794861201572892L;

    private Map<String,Serializable> additionObjects;

    public ExtRemoteInvocationResult(RemoteInvocationResult parent) {
        super();
        super.setValue(parent.getValue());
        super.setException(parent.getException());
        this.additionObjects = new HashMap<String, Serializable>();
    }

    public ExtRemoteInvocationResult(Object value) {
        super(value);
        this.additionObjects = new HashMap<String, Serializable>();
    }

    public ExtRemoteInvocationResult(Throwable exception) {
        super(exception);
        this.additionObjects = new HashMap<String, Serializable>();
    }

    public ExtRemoteInvocationResult() {
        this.additionObjects = new HashMap<String, Serializable>();
    }

    public void addAdditionObject(String key,Serializable value) {
        additionObjects.put(key,value);
    }

    public Serializable getAdditionObject(String key) {
        return additionObjects.get(key);
    }
}
