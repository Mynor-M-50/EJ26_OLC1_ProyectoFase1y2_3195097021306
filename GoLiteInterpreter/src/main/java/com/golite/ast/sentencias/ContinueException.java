package com.golite.ast.sentencias;

public class ContinueException extends RuntimeException {
    public ContinueException() {
        super("continue");
    }
}