package com.cs.distr.api;

import com.cs.distr.shared.api.ClientService;
import com.cs.distr.shared.api.entity.UserAccountEntity;
import com.cs.distr.shared.api.entity.UserEntity;
import com.cs.distr.shared.api.transaction.annotation.Distributed;
import com.cs.distr.shared.api.transaction.manager.OperationType;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * @author: artsiom.kotov
 */
public class ClientSecondServiceImpl implements ClientService<UserAccountEntity> {


    private ClientService firstClientService;

    private MongoOperations mongoOperations;

    public ClientService getFirstClientService() {
        return firstClientService;
    }

    public void setFirstClientService(ClientService firstClientService) {
        this.firstClientService = firstClientService;
    }

    public MongoOperations getMongoOperations() {
        return mongoOperations;
    }

    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "secondClientTS", operationType = OperationType.CREATE)
    public UserAccountEntity create(UserAccountEntity entity) {
        mongoOperations.save(entity);
        return entity;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "secondClientTS", operationType = OperationType.UPDATE)
    public UserAccountEntity update(UserAccountEntity entity) {
        mongoOperations.save(entity);
        return entity;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "secondClientTS", operationType = OperationType.UPDATE)
    public UserAccountEntity delete(UserAccountEntity entity) {
        mongoOperations.remove(entity);
        return entity;
    }

    @Override
    @Transactional
    @Distributed(serviceId = "secondClientTS", operationType = OperationType.CREATE)
    public void someAction(UserAccountEntity entity, boolean negative) {
        String mark = negative ? "negative_" : "success_";
        UserEntity  userEntity = new UserEntity(mark+entity.getId(),mark+"firstEntity",mark+"firstEntity",mark+"firstEntity");
        mongoOperations.save(entity);
        firstClientService.someAction(userEntity,negative);
    }

    @Override
    public UserAccountEntity findById(Object id) {
        return mongoOperations.findById(id, UserAccountEntity.class);
    }

    @Override
    public List<UserAccountEntity> findAll() {
        return mongoOperations.findAll(UserAccountEntity.class);
    }

}
