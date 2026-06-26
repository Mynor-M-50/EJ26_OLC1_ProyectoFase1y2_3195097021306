package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.Map;

public class NodoAsignacionAtributo extends NodoAST {
    public String nombreVar;
    public String atributo;
    public NodoAST valor;

    public NodoAsignacionAtributo(String nombreVar, String atributo, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.nombreVar = nombreVar;
        this.atributo  = atributo;
        this.valor     = valor;
    }

    @Override
    public Object interpretar() {
        Object obj = Entorno.getInstancia().obtener(nombreVar, linea, columna);

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

        instancia.put(atributo, valor.interpretar());
        return null;
    }
}