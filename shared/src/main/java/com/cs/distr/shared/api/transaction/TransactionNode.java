package com.cs.distr.shared.api.transaction;

import com.cs.distr.shared.api.transaction.manager.OperationType;

import javax.transaction.Transaction;
import java.io.Serializable;

/**
 * @author: artsiom.kotov
 */
public interface TransactionNode extends Transaction, Serializable {

    String getServiceId();

    Serializable getOldObject();

    void setOldObject(Serializable obj);

    Serializable getNewObject();

    void setNewObject(Serializable obj);

    OperationType getOperationType();

    void setStatus(int status);


}
