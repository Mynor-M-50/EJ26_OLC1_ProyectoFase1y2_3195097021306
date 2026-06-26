package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;
import com.golite.interpreter.Interprete;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodoInstanciaStruct extends NodoAST {
    public String tipoStruct;
    public List<Object[]> valores; // cada Object[]: [nombreAtributo, NodoAST valor]

    public NodoInstanciaStruct(String tipoStruct, List<Object[]> valores, int linea, int columna) {
        super(linea, columna);
        this.tipoStruct = tipoStruct;
        this.valores    = valores;
    }

    @Override
    public Object interpretar() {
        NodoStruct def = Entorno.obtenerStruct(tipoStruct, linea, columna);
        Map<String, Object> instancia = new HashMap<>();
        instancia.put("__tipo__", tipoStruct);

        // Inicializar con valores por defecto
        for (Object[] attr : def.atributos) {
            String tipo   = (String) attr[0];
            String nombre = (String) attr[1];
            instancia.put(nombre, valorPorDefecto(tipo));
        }

        // Asignar valores dados
        for (Object[] val : valores) {
            String nombre = (String) val[0];
            NodoAST expr  = (NodoAST) val[1];
            instancia.put(nombre, expr.interpretar());
        }

        return instancia;
    }

    private Object valorPorDefecto(String tipo) {
        switch (tipo) {
            case "int":     return 0;
            case "float64": return 0.0;
            case "string":  return "";
            case "bool":    return false;
            case "rune":    return '\0';
            default:        return null;
        }
    }
}