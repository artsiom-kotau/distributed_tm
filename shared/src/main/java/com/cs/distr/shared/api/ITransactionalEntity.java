package com.cs.distr.shared.api;

import java.io.Serializable;

/**
 * @author: artsiom.kotov
 */
public interface ITransactionalEntity extends Serializable {
    void setTid(String tid );

    String getTid();
}
