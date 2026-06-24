package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.ast.sentencias.NodoFuncion;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.ArrayList;
import java.util.List;

public class NodoLlamada extends NodoAST {
    public String nombre;
    public List<NodoAST> argumentos;

    public NodoLlamada(String nombre, List<NodoAST> argumentos, int linea, int columna) {
        super(linea, columna);
        this.nombre     = nombre;
        this.argumentos = argumentos;
    }

    @Override
    public Object interpretar() {
        NodoFuncion funcion = Entorno.obtenerFuncion(nombre, linea, columna);

        List<Object> args = new ArrayList<>();
        for (NodoAST arg : argumentos)
            args.add(arg.interpretar());

        if (funcion.parametros.size() != args.size()) {
            String msg = "Funcion '" + nombre + "' espera " + funcion.parametros.size()
                    + " argumentos, se dieron " + args.size() + ", linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        return funcion.ejecutar(args);
    }
}