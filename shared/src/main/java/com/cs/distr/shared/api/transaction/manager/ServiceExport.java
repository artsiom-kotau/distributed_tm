package com.cs.distr.shared.api.transaction.manager;

import java.io.Serializable;

/**
 * @author: artsiom.kotov
 */
public class ServiceExport implements Serializable {

    private static final long serialVersionUID = 7836286918271872384L;

    private String serviceId;
    private String serviceUrl;
    private Class serviceClass;

    public ServiceExport(String serviceId, String serviceUrl, Class serviceClass) {
        this.serviceId = serviceId;
        this.serviceUrl = serviceUrl;
        this.serviceClass = serviceClass;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String url) {
        this.serviceUrl = url;
    }

    public Class getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class serviceClass) {
        this.serviceClass = serviceClass;
    }
}
