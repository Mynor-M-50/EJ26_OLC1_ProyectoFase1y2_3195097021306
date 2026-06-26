package com.golite.ast.expresiones;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.Map;

public class NodoAccesoAtributo extends NodoAST {
    public NodoAST objeto;
    public String atributo;

    public NodoAccesoAtributo(NodoAST objeto, String atributo, int linea, int columna) {
        super(linea, columna);
        this.objeto    = objeto;
        this.atributo  = atributo;
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
        if (!instancia.containsKey(atributo)) {
            String msg = "Atributo '" + atributo + "' no existe en el struct, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        return instancia.get(atributo);
    }
}