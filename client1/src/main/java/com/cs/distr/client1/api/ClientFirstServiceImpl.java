package com.cs.distr.client1.api;

import com.cs.distr.shared.api.ClientService;
import com.cs.distr.shared.api.entity.UserEntity;
import com.cs.distr.shared.api.transaction.annotation.Distributed;
import com.cs.distr.shared.api.transaction.manager.OperationType;
import com.cs.distr.shared.api.transaction.store.TidCash;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author: artsiom.kotov
 */
public class ClientFirstServiceImpl implements ClientService<UserEntity> {

    private static final long serialVersionUID = 8393260026670019518L;

    private MongoOperations mongoOperations;

    public MongoOperations getMongoTemplate() {
        return mongoOperations;
    }

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoOperations = mongoTemplate;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "firstClientTS", operationType = OperationType.CREATE)
    public UserEntity create(UserEntity entity) {
        mongoOperations.save(entity);
        return entity;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "firstClientTS", operationType = OperationType.UPDATE)
    public UserEntity update(UserEntity entity) {
        mongoOperations.save(entity);
        return entity;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "firstClientTS", operationType = OperationType.UPDATE)
    public UserEntity delete(UserEntity entity) {
        mongoOperations.remove(entity);
        return entity;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "firstClientTS", operationType = OperationType.CREATE)
    public void someAction(UserEntity entity, boolean negative) {
        create(entity);
        if (negative) {
            throw new RuntimeException("Exception in ClientFirstService.someAction; Tid: "+ TidCash.getTid(Thread.currentThread().getId()).getUuid());
        }
    }

    @Override
    public UserEntity findById(Object id) {
        return mongoOperations.findById(id, UserEntity.class);
    }

    @Override
    public List<UserEntity> findAll() {
        return mongoOperations.findAll(UserEntity.class);
    }
}
