package com.cs.distr.shared.api.transaction.annotation;

import com.cs.distr.shared.api.transaction.manager.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: artsiom.kotov
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Distributed {
    String serviceId();

    OperationType operationType();
}
