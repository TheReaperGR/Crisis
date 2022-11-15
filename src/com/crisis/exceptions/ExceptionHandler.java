package com.crisis.exceptions;

public class ExceptionHandler extends RuntimeException  {

    public ExceptionHandler(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
