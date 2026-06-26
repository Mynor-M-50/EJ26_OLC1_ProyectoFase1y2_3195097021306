package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import java.util.List;

public class NodoStruct extends NodoAST {
    public String nombre;
    public List<Object[]> atributos; // cada Object[]: [tipo, nombre]

    public NodoStruct(String nombre, List<Object[]> atributos, int linea, int columna) {
        super(linea, columna);
        this.nombre    = nombre;
        this.atributos = atributos;
    }

    @Override
    public Object interpretar() {
        Entorno.registrarStruct(nombre, this);
        return null;
    }
}