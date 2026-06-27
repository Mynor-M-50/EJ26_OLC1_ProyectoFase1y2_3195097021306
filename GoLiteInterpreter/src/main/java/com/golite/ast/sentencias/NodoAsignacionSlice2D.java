package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.List;

public class NodoAsignacionSlice2D extends NodoAST {
    public String nombre;
    public NodoAST indiceFila;
    public NodoAST indiceCol;
    public NodoAST valor;

    public NodoAsignacionSlice2D(String nombre, NodoAST indiceFila, NodoAST indiceCol, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.nombre     = nombre;
        this.indiceFila = indiceFila;
        this.indiceCol  = indiceCol;
        this.valor      = valor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object interpretar() {
        Object s = Entorno.getInstancia().obtener(nombre, linea, columna);

        if (s == null) {
            String msg = "Operacion invalida sobre nil, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        if (!(s instanceof List)) {
            String msg = "No es un slice 2D, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        Object iFila = indiceFila.interpretar();
        Object iCol  = indiceCol.interpretar();
        Object v     = valor.interpretar();

        if (!(iFila instanceof Integer) || !(iCol instanceof Integer)) {
            String msg = "Indice debe ser entero, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> matriz = (List<Object>) s;
        int fila = (Integer) iFila;
        int col  = (Integer) iCol;

        if (fila < 0 || fila >= matriz.size()) {
            String msg = "Indice de fila fuera de rango: " + fila + ", linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        Object filaObj = matriz.get(fila);
        if (!(filaObj instanceof List)) {
            String msg = "Elemento no es un slice, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        List<Object> filaLista = (List<Object>) filaObj;

        if (col < 0 || col >= filaLista.size()) {
            String msg = "Indice de columna fuera de rango: " + col + ", linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        filaLista.set(col, v);
        return null;
    }
}