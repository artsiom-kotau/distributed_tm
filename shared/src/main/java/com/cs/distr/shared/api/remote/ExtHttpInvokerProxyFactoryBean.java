package com.cs.distr.shared.api.remote;

import com.cs.distr.shared.api.transaction.store.Tid;
import com.cs.distr.shared.api.transaction.store.TidCash;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

/**
 * @author: artsiom.kotov
 */
public class ExtHttpInvokerProxyFactoryBean extends HttpInvokerProxyFactoryBean {
    protected static final String TID_ATTR = "tid";

    @Override
    protected RemoteInvocationResult executeRequest(RemoteInvocation invocation, MethodInvocation originalInvocation) throws Exception {
        extendInvocation(invocation);
        RemoteInvocationResult result = super.executeRequest(invocation, originalInvocation);
        return extendResult(result);
    }

    protected void extendInvocation(RemoteInvocation invocation) {
        if (TidCash.isTidExist(Thread.currentThread().getId()) && invocation.getAttribute(TID_ATTR) == null) {
            invocation.addAttribute(TID_ATTR, TidCash.getTid(Thread.currentThread().getId()));
        }
    }

    protected ExtRemoteInvocationResult extendResult(RemoteInvocationResult result) {
        ExtRemoteInvocationResult extendsResult;
        if (result instanceof ExtRemoteInvocationResult) {
            extendsResult = (ExtRemoteInvocationResult) result;
            Tid tid = (Tid) extendsResult.getAdditionObject(TID_ATTR);
            if (tid != null) {
                if (tid.isAlive()) {
                    TidCash.addTid(Thread.currentThread().getId(), tid);
                } else {
                    TidCash.removeKeysByTid(tid);
                }
            }
        } else {
            extendsResult = new ExtRemoteInvocationResult(result);
        }
        return extendsResult;
    }
}
