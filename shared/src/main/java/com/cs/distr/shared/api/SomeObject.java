package com.cs.distr.shared.api;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: artsiom.kotov
 */
@Entity
public class SomeObject implements Serializable {
    static final long serialVersionUID = 42L;

    private String name;
    private Date date;

    public SomeObject(String name) {
        this.name = name;
        this.date = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "SomeObject{" +
                "name='" + name + '\'' +
                ", date=" + date +
                '}';
    }
}
