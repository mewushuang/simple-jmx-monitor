package com.van.monitor.api;

/**
 * 直接用==进行比较，适用enum
 */
public class SimpleAtomicStatus<T> {

    private T val;

    public SimpleAtomicStatus(T val) {
        this.val = val;
    }

    public boolean inAndSet(T newValue, T... expects) {
        synchronized (this) {
            for (T t : expects) {
                if (val == t) {
                    val = newValue;
                    return true;
                }
            }
            return false;
        }
    }


    public boolean notInAndSet(T newValue, T... expects) {
        boolean notIn=true;
        synchronized (this) {
            for (T t : expects) {
                if (val == t) {
                    notIn=false;
                }
            }
            if (notIn) val=newValue;
            return notIn;
        }
    }

    public synchronized T get() {
        return val;
    }

    public synchronized T set(T val) {
        T tmp = this.val;
        this.val = val;
        return tmp;
    }


}
