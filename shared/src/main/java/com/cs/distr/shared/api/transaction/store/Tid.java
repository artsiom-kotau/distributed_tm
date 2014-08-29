package com.cs.distr.shared.api.transaction.store;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: artsiom.kotov
 */
public class Tid implements Serializable{

    private static final long serialVersionUID = 6960481547739173285L;

    private String uuid;
    private boolean alive;


    public Tid(String uuid) {
        this.uuid = uuid;
        this.alive = true;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        this.alive = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tid)) {
            return false;
        }

        Tid tid = (Tid) o;

        return uuid.equals(tid.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "uid: "+uuid;
    }
}
