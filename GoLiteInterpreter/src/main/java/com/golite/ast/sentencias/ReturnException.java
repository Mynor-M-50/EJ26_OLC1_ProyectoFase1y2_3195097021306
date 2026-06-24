package com.golite.ast.sentencias;

public class ReturnException extends RuntimeException {
    public final Object valor;

    public ReturnException(Object valor) {
        super("return");
        this.valor = valor;
    }
}