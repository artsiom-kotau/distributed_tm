package com.cs.distr.shared.api.remote;

import com.cs.distr.shared.api.transaction.store.Tid;
import com.cs.distr.shared.api.transaction.store.TidCash;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.util.NestedServletException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: artsiom.kotov
 */
public class ExtHttpInvokerServiceExporter extends HttpInvokerServiceExporter {
    private static final String TID_ATTR = "tid";

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            RemoteInvocation invocation = readRemoteInvocation(request);
            RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
            clearTid();  //!!!!
            writeRemoteInvocationResult(request, response, result);

        }
        catch (ClassNotFoundException ex) {
            throw new NestedServletException("Class not found during deserialization", ex);
        }
    }

    @Override
    protected RemoteInvocation readRemoteInvocation(HttpServletRequest request) throws IOException, ClassNotFoundException {
        RemoteInvocation invocation = super.readRemoteInvocation(request);
        if (invocation.getAttribute(TID_ATTR) != null) {
            TidCash.addTid(Thread.currentThread().getId(),(Tid)invocation.getAttribute(TID_ATTR));
        }
        return invocation;
    }

    @Override
    protected RemoteInvocationResult invokeAndCreateResult(RemoteInvocation invocation, Object targetObject) {
        ExtRemoteInvocationResult remoteInvocationResult = new ExtRemoteInvocationResult(super.invokeAndCreateResult(invocation,targetObject));
        if (TidCash.isTidExist(Thread.currentThread().getId())) {
            remoteInvocationResult.addAdditionObject(TID_ATTR,TidCash.getTid(Thread.currentThread().getId()));
        }
        return remoteInvocationResult;
    }

    private void clearTid() {
        Tid tid = TidCash.getTid(Thread.currentThread().getId());
        if (tid != null && !tid.isAlive()) {
            TidCash.removeKeysByTid(tid);
        }
    }
}
