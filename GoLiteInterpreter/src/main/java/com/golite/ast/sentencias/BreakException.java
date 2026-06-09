package com.golite.ast.sentencias;

public class BreakException extends RuntimeException {
    public BreakException() {
        super("break");
    }
}