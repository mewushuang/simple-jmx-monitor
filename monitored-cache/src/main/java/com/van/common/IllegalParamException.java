package com.van.common;

/**
 * Created by van on 2016/12/6.
 */
public class IllegalParamException extends PersistanseException {


    public IllegalParamException(String message) {
        super(message);
    }

    public IllegalParamException(String message, Throwable cause) {
        super(message, cause);
    }


}
