package com.cs.distr.api;

import com.cs.distr.shared.api.remote.transactional.ITransactionalService;
import com.cs.distr.shared.api.transaction.manager.Change;
import com.cs.distr.shared.api.transaction.manager.OperationType;
import com.cs.distr.shared.api.transaction.store.TidCash;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: artsiom.kotov
 */
public class ClientSecondTransactionalService implements ITransactionalService {

    private static final long serialVersionUID = -3035752401043228088L;

    private String serviceId;

    private MongoOperations mongoOperations;

    public ClientSecondTransactionalService(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    public MongoOperations getMongoOperations() {
        return mongoOperations;
    }

    public void setMongoOperations(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public void commit(Change change) {
        if (change.getOperationType() != OperationType.DELETE) {
            try {
                Object targetObject = change.getNewObject();
                Query removeQuery = createFindQuery(targetObject);
                Update update = new Update().unset("tid");
                mongoOperations.findAndModify(removeQuery, update, targetObject.getClass());
            } catch (Exception exc) {
                throw new RuntimeException(exc);
            }
        }
    }

    @Override
    public void rollback(Change change) {
        OperationType operationType = change.getOperationType();
        try {
            switch (operationType) {
                case CREATE: {
                    Object targetObject = change.getNewObject();
                    Query removeQuery = createFindQuery(targetObject);
                    Object removedObject = mongoOperations.findOne(removeQuery,targetObject.getClass());
                    if (removedObject != null) {
                        mongoOperations.remove(removedObject);
                    }
                    break;
                }
                case UPDATE:
                case DELETE: {
                    mongoOperations.save(change.getOldObject());
                    break;
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    @Override
    public <ObjectType> ObjectType fetch(ObjectType fetchedObject, OperationType operationType, Class<ObjectType> clazz) {
        ObjectType object = null;
        try {
            switch (operationType) {
                case UPDATE:
                case DELETE: {
                    Object id = getId(fetchedObject);
                    if (id == null || "".equals(id)) {
                        throw new IllegalStateException("Object has undefined field");
                    }
                    object = mongoOperations.findById(id,clazz);
                    break;
                }
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        return object;
    }

    private Query createFindQuery(Object targetObject) throws Exception{
        Map<String,Object> objectProperties = convertObjectToMap(targetObject);
        Query query = new Query();
        for(Map.Entry<String, Object> field : objectProperties.entrySet()) {
            String fieldKey = field.getKey();
            Object fieldValue = field.getValue();
            if (fieldKey.equals("class")) {
                fieldKey = "_"+fieldKey;
                fieldValue = ((Class)fieldValue).getName();
            } else if (fieldKey.equals("id")) {
                fieldKey = "_"+fieldKey;
            }

            if (fieldValue != null) {
                query.addCriteria(Criteria.where(fieldKey).is(fieldValue));
            }
        }
        return query;
    }

    public Map<String, Object> convertObjectToMap(Object obj) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        BeanInfo info = Introspector.getBeanInfo(obj.getClass());
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method reader = pd.getReadMethod();
            if (reader != null)
                result.put(pd.getName(), reader.invoke(obj));
        }
        return result;
    }

    public Object getId(Object targetObject) throws Exception{
        Object id = null;
        for(Field field : targetObject.getClass().getDeclaredFields()){
            if (field.isAnnotationPresent(Id.class)) {
                id = BeanUtils.getProperty(targetObject, field.getName());
                break;
            }
        }
        return id;
    }
}
