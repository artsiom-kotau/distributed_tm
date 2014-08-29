package com.cs.distr.shared.api;

/**
 * @author: artsiom.kotov
 */
public abstract class AbstractTransactionEntity implements ITransactionalEntity {

    private String tid;

    @Override
    public void setTid(String tid) {
        this.tid = tid;
    }

    @Override
    public String getTid() {
        return tid;
    }
}
