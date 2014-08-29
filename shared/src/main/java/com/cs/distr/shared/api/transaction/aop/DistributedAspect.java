package com.cs.distr.shared.api.transaction.aop;

import com.cs.distr.shared.api.ITransactionalEntity;
import com.cs.distr.shared.api.remote.transactional.ITransactionalService;
import com.cs.distr.shared.api.transaction.annotation.Distributed;
import com.cs.distr.shared.api.transaction.manager.Change;
import com.cs.distr.shared.api.transaction.manager.ChangeObject;
import com.cs.distr.shared.api.transaction.manager.DistributeTransactionManager;
import com.cs.distr.shared.api.transaction.manager.OperationType;
import com.cs.distr.shared.api.transaction.store.Tid;
import com.cs.distr.shared.api.transaction.store.TidCash;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Entity;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: artsiom.kotov
 */
@Aspect
public class DistributedAspect {

    private static List<Class> suitableEntityClasses = new CopyOnWriteArrayList<Class>();
    private static Class[] targetEntityAnnotations = {Entity.class, Document.class};

    private DistributeTransactionManager transactionManager;
    private ITransactionalService transactionalService;


    public DistributedAspect(DistributeTransactionManager transactionManager, ITransactionalService transactionalService) {
        this.transactionManager = transactionManager;
        this.transactionalService = transactionalService;
    }

    @Pointcut("execution(* *.*(..)) && @annotation(org.springframework.transaction.annotation.Transactional)")
    public void distributedPointcut() {
    }

    @Pointcut("execution(* com.cs.distr.shared.api.remote.transactional.ITransactionalService.commit(..))")
    public void transactionalCommitAction() {}

    @Pointcut("execution(* com.cs.distr.shared.api.remote.transactional.ITransactionalService.rollback(..))")
    public void transactionalRollbackAction() {}

    @Before(value = "distributedPointcut() && @annotation(distributed)")
    public void collectTransactionalInfo(JoinPoint joinPoint, Distributed distributed) {
        Tid tid = TidCash.getTid(Thread.currentThread().getId());
        String serviceId = distributed.serviceId();
        OperationType operationType = distributed.operationType();
        for(Object targetObject : joinPoint.getArgs()) {
            if (checkIsEntity(targetObject)) {
                Change change = buildChangeObject(targetObject,tid.getUuid(),serviceId,operationType);
                initAdditionalInfo(targetObject,tid.getUuid());
                transactionManager.storeTransactionInfo(change);
            }
        }
        System.out.println("Aspect work");
    }

    @After(value = "transactionalCommitAction() || transactionalRollbackAction()")
    public void clearTids() {
        Tid tid = TidCash.getTid(Thread.currentThread().getId());
        if (tid != null) {
            TidCash.removeKeysByTid(tid);
        }
    }

    private boolean checkIsEntity(Object object) {
        boolean isSuitableEntity = false;
        if (suitableEntityClasses.contains(object.getClass())) {
            isSuitableEntity = true;
        } else {
            for(Class annotationClass : targetEntityAnnotations) {
                isSuitableEntity = object.getClass().isAnnotationPresent(annotationClass);
                if (isSuitableEntity) {
                    suitableEntityClasses.add(object.getClass());
                    break;
                }
            }
        }
        return isSuitableEntity;
    }

    private Change buildChangeObject(Object targetObject, String tid, String serviceId, OperationType operationType) {
        Class clazz = targetObject.getClass();
        Object oldObject = transactionalService.fetch(targetObject,operationType,clazz);
        return new ChangeObject(tid,serviceId,oldObject,targetObject,operationType);
    }

    private void initAdditionalInfo(Object targetObject, String tid) {
        if (targetObject instanceof ITransactionalEntity) {
            ((ITransactionalEntity) targetObject).setTid(tid);
        }
    }
}
