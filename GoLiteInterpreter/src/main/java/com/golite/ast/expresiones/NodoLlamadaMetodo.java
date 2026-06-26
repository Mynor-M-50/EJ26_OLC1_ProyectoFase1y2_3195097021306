package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.ast.sentencias.NodoFuncionStruct;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodoLlamadaMetodo extends NodoAST {
    public NodoAST objeto;
    public String metodo;
    public List<NodoAST> argumentos;

    public NodoLlamadaMetodo(NodoAST objeto, String metodo, List<NodoAST> argumentos, int linea, int columna) {
        super(linea, columna);
        this.objeto     = objeto;
        this.metodo     = metodo;
        this.argumentos = argumentos;
    }

    @Override
    public Object interpretar() {
        Object obj = objeto.interpretar();

        if (!(obj instanceof Map)) {
            String msg = "No es un struct, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        Map<String, Object> instancia = (Map<String, Object>) obj;
        String tipoStruct = (String) instancia.get("__tipo__");

        NodoFuncionStruct funcion = Entorno.obtenerMetodo(tipoStruct, metodo, linea, columna);

        List<Object> args = new ArrayList<>();
        args.add(instancia); // primer argumento es la referencia al struct
        for (NodoAST arg : argumentos)
            args.add(arg.interpretar());

        return funcion.ejecutar(args);
    }
}