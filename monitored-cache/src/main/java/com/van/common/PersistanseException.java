package com.van.common;

/**
 * Created by van on 2016/12/10.
 */
public class PersistanseException extends RuntimeException {

    public PersistanseException(String s) {
        super(s);
    }

    public PersistanseException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public PersistanseException(Throwable throwable) {
        super(throwable);
    }
}
