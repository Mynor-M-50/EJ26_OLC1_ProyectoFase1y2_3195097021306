package com.golite.interpreter;

import java.util.HashMap;
import java.util.Map;

public class Entorno {

    // Singleton una sola instancia global
    private static Entorno instancia;

    public static Entorno getInstancia() {
        if (instancia == null)
            instancia = new Entorno(null);
        return instancia;
    }

    public static void resetear() {
        instancia = new Entorno(null);
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
    private final Entorno padre; // entorno anterior (null si es global)

    public Entorno(Entorno padre) {
        this.tabla = new HashMap<>();
        this.padre = padre;
    }

    // Crear un nuevo entorno hijo (al entrar a un bloque)
    public Entorno crearHijo() {
        return new Entorno(this);
    }

    // Declarar una variable en el entorno actual
    public void declarar(String nombre, String tipo, Object valor, int linea, int columna) {
        if (tabla.containsKey(nombre))
            throw new RuntimeException("Variable '" + nombre + "' ya declarada en este ámbito, línea " + linea);
        tabla.put(nombre, new Variable(tipo, valor));
    }

    // Obtener el valor de una variable
    public Object obtener(String nombre, int linea, int columna) {
        if (tabla.containsKey(nombre))
            return tabla.get(nombre).valor;
        if (padre != null)
            return padre.obtener(nombre, linea, columna);
        throw new RuntimeException("Variable '" + nombre + "' no declarada, línea " + linea);
    }

    // Obtener el tipo de una variable
    public String obtenerTipo(String nombre, int linea, int columna) {
        if (tabla.containsKey(nombre))
            return tabla.get(nombre).tipo;
        if (padre != null)
            return padre.obtenerTipo(nombre, linea, columna);
        throw new RuntimeException("Variable '" + nombre + "' no declarada, línea " + linea);
    }

    // Asignar un nuevo valor a una variable ya declarada
    public void asignar(String nombre, Object valor, int linea, int columna) {
        if (tabla.containsKey(nombre)) {
            Variable var = tabla.get(nombre);

            // Verificar compatibilidad de tipos
            verificarTipo(var.tipo, valor, linea);
            var.valor = valor;
            return;
        }
        if (padre != null) {
            padre.asignar(nombre, valor, linea, columna);
            return;
        }
        throw new RuntimeException("Variable '" + nombre + "' no declarada, línea " + linea);
    }

    // Verificar que el tipo sea compatible
    private void verificarTipo(String tipo, Object valor, int linea) {
        if (valor == null) return;
        switch (tipo) {
            case "int":
                if (!(valor instanceof Integer))
                    throw new RuntimeException("Tipo incompatible: se esperaba int, línea " + linea);
                break;
            case "float64":
                if (!(valor instanceof Double) && !(valor instanceof Integer))
                    throw new RuntimeException("Tipo incompatible: se esperaba float64, línea " + linea);
                break;
            case "string":
                if (!(valor instanceof String))
                    throw new RuntimeException("Tipo incompatible: se esperaba string, línea " + linea);
                break;
            case "bool":
                if (!(valor instanceof Boolean))
                    throw new RuntimeException("Tipo incompatible: se esperaba bool, línea " + linea);
                break;
            case "rune":
                if (!(valor instanceof Character))
                    throw new RuntimeException("Tipo incompatible: se esperaba rune, línea " + linea);
                break;
        }
    }

    // Obtener toda la tabla para el reporte de símbolos
    public Map<String, Object[]> obtenerTabla() {
        Map<String, Object[]> resultado = new HashMap<>();
        for (Map.Entry<String, Variable> entry : tabla.entrySet())
            resultado.put(entry.getKey(), new Object[]{entry.getValue().tipo, entry.getValue().valor});
        if (padre != null)
            resultado.putAll(padre.obtenerTabla());
        return resultado;
    }
}