package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;

public class NodoIdentificador extends NodoAST {
    public String nombre;

    public NodoIdentificador(String nombre, int linea, int columna) {
        super(linea, columna);
        this.nombre = nombre;
    }

    @Override
    public Object interpretar() {
        return Entorno.getInstancia().obtener(nombre, linea, columna);
    }
}
