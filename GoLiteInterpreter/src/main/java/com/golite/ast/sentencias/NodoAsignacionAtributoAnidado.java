package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.Map;

public class NodoAsignacionAtributoAnidado extends NodoAST {
    public String nombreVar;
    public String atributo1;
    public String atributo2;
    public NodoAST valor;

    public NodoAsignacionAtributoAnidado(String nombreVar, String atributo1, String atributo2, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.nombreVar = nombreVar;
        this.atributo1 = atributo1;
        this.atributo2 = atributo2;
        this.valor     = valor;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object interpretar() {
        Object obj = Entorno.getInstancia().obtener(nombreVar, linea, columna);

        if (!(obj instanceof Map)) {
            String msg = "No es un struct, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        Map<String, Object> instancia = (Map<String, Object>) obj;
        Object sub = instancia.get(atributo1);

        if (!(sub instanceof Map)) {
            String msg = "Atributo '" + atributo1 + "' no es un struct, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }

        Map<String, Object> subInstancia = (Map<String, Object>) sub;
        subInstancia.put(atributo2, valor.interpretar());
        return null;
    }
}