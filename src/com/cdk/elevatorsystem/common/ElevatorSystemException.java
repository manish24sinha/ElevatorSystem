package com.cdk.elevatorsystem.common;

/**
 * Created by msinha on 5/13/17.
 */
public class ElevatorSystemException extends Exception {
    private String message = null;
    private Exception exception;


    public ElevatorSystemException(final String message) {
        this.message = message;
    }

    public ElevatorSystemException(final Exception exception, final String message) {
        this.exception = exception;
        this.message = message;
    }


    public Exception getException() {
        return exception;
    }


    public String getMessage() {
        return message + "\n" + super.getMessage();
    }
}
