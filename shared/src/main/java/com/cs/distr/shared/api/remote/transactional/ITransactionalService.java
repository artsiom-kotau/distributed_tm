package com.cs.distr.shared.api.remote.transactional;

import com.cs.distr.shared.api.transaction.manager.Change;
import com.cs.distr.shared.api.transaction.manager.OperationType;

import java.io.Serializable;

/**
 * @author: artsiom.kotov
 */
public interface ITransactionalService extends Serializable {

    String getServiceId();

    <ObjectType> ObjectType  fetch(ObjectType fetchedObject, OperationType operationType, Class<ObjectType> clazz);

    void rollback(Change change);

    void commit(Change change);
}
