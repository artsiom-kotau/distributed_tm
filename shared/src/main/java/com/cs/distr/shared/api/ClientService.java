package com.cs.distr.shared.api;

import java.util.List;

/**
 * @author: artsiom.kotov
 */
public interface ClientService<TransactionEntity extends ITransactionalEntity> {

    TransactionEntity create(TransactionEntity entity);

    TransactionEntity update(TransactionEntity entity);

    TransactionEntity delete(TransactionEntity entity);

    TransactionEntity findById(Object id);

    List<TransactionEntity> findAll();

    void someAction(TransactionEntity entity, boolean negative);
}
