package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import java.util.List;
import java.util.Map;

public class NodoPrintln extends NodoAST {
    public List<NodoAST> argumentos;

    public NodoPrintln(List<NodoAST> argumentos, int linea, int columna) {
        super(linea, columna);
        this.argumentos = argumentos;
    }

    @Override
    public Object interpretar() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < argumentos.size(); i++) {
            Object val = argumentos.get(i).interpretar();
            sb.append(formatear(val));
            if (i < argumentos.size() - 1)
                sb.append(" ");
        }

        String resultado = sb.toString();
        com.golite.interpreter.Interprete.getInstancia().agregarConsola(resultado);
        return resultado;
    }

    @SuppressWarnings("unchecked")
    public static String formatear(Object val) {
        if (val == null) return "nil";

        if (val instanceof Character) return String.valueOf((Character) val);

        if (val instanceof List) {
            List<Object> lista = (List<Object>) val;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < lista.size(); i++) {
                sb.append(formatear(lista.get(i)));
                if (i < lista.size() - 1) sb.append(" ");
            }
            sb.append("]");
            return sb.toString();
        }

        if (val instanceof Map) {
            Map<String, Object> mapa = (Map<String, Object>) val;
            String tipo = (String) mapa.get("__tipo__");
            StringBuilder sb = new StringBuilder();
            if (tipo != null) sb.append(tipo);
            sb.append("{");
            boolean primero = true;
            for (Map.Entry<String, Object> entry : mapa.entrySet()) {
                if ("__tipo__".equals(entry.getKey())) continue;
                if (!primero) sb.append(", ");
                sb.append(entry.getKey()).append(": ").append(formatear(entry.getValue()));
                primero = false;
            }
            sb.append("}");
            return sb.toString();
        }

        return val.toString();
    }
}