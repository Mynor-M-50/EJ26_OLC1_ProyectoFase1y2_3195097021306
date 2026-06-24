package com.golite.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Entorno {

    // Singleton = una sola instancia global
    private static Entorno instancia;

    private static final Map<String, com.golite.ast.sentencias.NodoFuncion> funciones = new HashMap<>();

    public static void registrarFuncion(String nombre, com.golite.ast.sentencias.NodoFuncion funcion) {
        funciones.put(nombre, funcion);
    }

    public static com.golite.ast.sentencias.NodoFuncion obtenerFuncion(String nombre, int linea, int columna) {
        if (funciones.containsKey(nombre))
            return funciones.get(nombre);
        String msg = "Funcion '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    public static Entorno getInstancia() {
        if (instancia == null)
            instancia = new Entorno(null);
        return instancia;
    }

    public static void resetear() {
        instancia = new Entorno(null);
        funciones.clear();
    }

    // Cada variable tiene nombre, tipo y valor
    private static class Variable {
        String tipo;
        Object valor;
        Variable(String tipo, Object valor) {
            this.tipo  = tipo;
            this.valor = valor;
        }
    }

    private final Map<String, Variable> tabla;
    private final Entorno padre;

    public Entorno(Entorno padre) {
        this.tabla = new HashMap<>();
        this.padre = padre;
    }

    // Crear un nuevo entorno hijo (al entrar a un bloque)
    public Entorno crearHijo() {
        return new Entorno(this);
    }

    // Declarar variable
    public void declarar(String nombre, String tipo, Object valor, int linea, int columna) {
        if (tabla.containsKey(nombre)) {
            String msg = "Variable '" + nombre + "' ya declarada en este ambito, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        tabla.put(nombre, new Variable(tipo, valor));
    }

    // Obtener valor
    public Object obtener(String nombre, int linea, int columna) {
        if (tabla.containsKey(nombre))
            return tabla.get(nombre).valor;
        if (padre != null)
            return padre.obtener(nombre, linea, columna);
        String msg = "Variable '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    // Obtener tipo
    public String obtenerTipo(String nombre, int linea, int columna) {
        if (tabla.containsKey(nombre))
            return tabla.get(nombre).tipo;
        if (padre != null)
            return padre.obtenerTipo(nombre, linea, columna);
        String msg = "Variable '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    // Asignar valor
    public void asignar(String nombre, Object valor, int linea, int columna) {
        if (tabla.containsKey(nombre)) {
            Variable var = tabla.get(nombre);
            verificarTipo(var.tipo, valor, linea, columna);
            var.valor = valor;
            return;
        }
        if (padre != null) {
            padre.asignar(nombre, valor, linea, columna);
            return;
        }
        String msg = "Variable '" + nombre + "' no declarada, lnea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    // Verificar compatibilidad de tipos
    private void verificarTipo(String tipo, Object valor, int linea, int columna) {
        if (valor == null) return;
        String msg = null;
        switch (tipo) {
            case "int":
                if (!(valor instanceof Integer))
                    msg = "Tipo incompatible: se esperaba int, linea " + linea;
                break;
            case "float64":
                if (!(valor instanceof Double) && !(valor instanceof Integer))
                    msg = "Tipo incompatible: se esperaba float64, linea " + linea;
                break;
            case "string":
                if (!(valor instanceof String))
                    msg = "Tipo incompatible: se esperaba string, liea " + linea;
                break;
            case "bool":
                if (!(valor instanceof Boolean))
                    msg = "Tipo incompatible: se esperaba bool, linea " + linea;
                break;
            case "rune":
                if (!(valor instanceof Character))
                    msg = "Tipo incompatible: se esperaba rune, linea " + linea;
                break;
        }
        if (msg != null) {
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
    }

    // Obtener tabla completa para reportes
    public Map<String, Object[]> obtenerTabla() {
        Map<String, Object[]> resultado = new HashMap<>();
        for (Map.Entry<String, Variable> entry : tabla.entrySet())
            resultado.put(entry.getKey(), new Object[]{entry.getValue().tipo, entry.getValue().valor});
        if (padre != null)
            resultado.putAll(padre.obtenerTabla());
        return resultado;
    }

    public static void pushBloque() {
        instancia = new Entorno(instancia);
    }

    public static void popBloque() {
        if (instancia.padre != null)
            instancia = instancia.padre;
    }
}