package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoAsignacionSlice extends NodoAST {
    public String nombre;
    public NodoAST indice;
    public NodoAST valor;

    public NodoAsignacionSlice(String nombre, NodoAST indice, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.nombre = nombre;
        this.indice = indice;
        this.valor  = valor;
    }

    @Override
    public Object interpretar() {
        Object s = Entorno.getInstancia().obtener(nombre, linea, columna);
        Object i = indice.interpretar();
        Object v = valor.interpretar();

        if (!(s instanceof List)) {
            String msg = "No es un slice, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        if (!(i instanceof Integer)) {
            String msg = "Indice debe ser entero, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> lista = (List<Object>) s;
        int idx = (Integer) i;

        if (idx < 0 || idx >= lista.size()) {
            String msg = "Indice fuera de rango: " + idx + ", linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        lista.set(idx, v);
        return null;
    }
}