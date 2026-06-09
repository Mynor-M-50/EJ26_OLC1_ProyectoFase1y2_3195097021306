package com.golite.errors;

public class ErrorSemantic {
    public String descripcion;
    public int linea;
    public int columna;
    public String tipo; // "lexico", "sintactico", "semntico"

    public ErrorSemantic(String descripcion, int linea, int columna, String tipo) {
        this.descripcion = descripcion;
        this.linea       = linea;
        this.columna     = columna;
        this.tipo        = tipo;
    }

    @Override
    public String toString() {
        return tipo + " | linea " + linea + ", col " + columna + " | " + descripcion;
    }
}