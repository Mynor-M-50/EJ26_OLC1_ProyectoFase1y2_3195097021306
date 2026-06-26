package com.golite.interpreter;

import com.golite.ast.sentencias.NodoFuncion;
import com.golite.ast.sentencias.NodoFuncionStruct;
import com.golite.ast.sentencias.NodoStruct;
import java.util.HashMap;
import java.util.Map;

public class Entorno {

    private static Entorno instancia;

    private static final Map<String, NodoFuncion> funciones         = new HashMap<>();
    private static final Map<String, NodoStruct>  structs           = new HashMap<>();
    private static final Map<String, Map<String, NodoFuncionStruct>> metodos = new HashMap<>();

    // --- Funciones ---
    public static void registrarFuncion(String nombre, NodoFuncion funcion) {
        funciones.put(nombre, funcion);
    }

    public static NodoFuncion obtenerFuncion(String nombre, int linea, int columna) {
        if (funciones.containsKey(nombre))
            return funciones.get(nombre);
        String msg = "Funcion '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    // --- Structs ---
    public static void registrarStruct(String nombre, NodoStruct struct) {
        structs.put(nombre, struct);
    }

    public static NodoStruct obtenerStruct(String nombre, int linea, int columna) {
        if (structs.containsKey(nombre))
            return structs.get(nombre);
        String msg = "Struct '" + nombre + "' no declarado, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    public static boolean existeStruct(String nombre) {
        return structs.containsKey(nombre);
    }

    // --- Metodos de structs ---
    public static void registrarMetodo(String tipoStruct, String nombre, NodoFuncionStruct metodo) {
        metodos.computeIfAbsent(tipoStruct, k -> new HashMap<>()).put(nombre, metodo);
    }

    public static NodoFuncionStruct obtenerMetodo(String tipoStruct, String nombre, int linea, int columna) {
        Map<String, NodoFuncionStruct> tabla = metodos.get(tipoStruct);
        if (tabla != null && tabla.containsKey(nombre))
            return tabla.get(nombre);
        String msg = "Metodo '" + nombre + "' no existe en struct '" + tipoStruct + "', linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    // --- Singleton ---
    public static Entorno getInstancia() {
        if (instancia == null)
            instancia = new Entorno(null);
        return instancia;
    }

    public static void resetear() {
        instancia = new Entorno(null);
        funciones.clear();
        structs.clear();
        metodos.clear();
    }

    // --- Variables ---
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

    public void declarar(String nombre, String tipo, Object valor, int linea, int columna) {
        if (tabla.containsKey(nombre)) {
            String msg = "Variable '" + nombre + "' ya declarada en este ambito, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        tabla.put(nombre, new Variable(tipo, valor));
    }

    public void declararStruct(String nombre, java.util.Map<String, Object> instancia, int linea, int columna) {
        String tipo = (String) instancia.get("__tipo__");
        tabla.put(nombre, new Variable(tipo, instancia));
    }

    public Object obtener(String nombre, int linea, int columna) {
        if (tabla.containsKey(nombre))
            return tabla.get(nombre).valor;
        if (padre != null)
            return padre.obtener(nombre, linea, columna);
        String msg = "Variable '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    public String obtenerTipo(String nombre, int linea, int columna) {
        if (tabla.containsKey(nombre))
            return tabla.get(nombre).tipo;
        if (padre != null)
            return padre.obtenerTipo(nombre, linea, columna);
        String msg = "Variable '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

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
        String msg = "Variable '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    private void verificarTipo(String tipo, Object valor, int linea, int columna) {
        if (valor == null || tipo == null) return;
        if (existeStruct(tipo)) return; // los structs no se verifican por tipo Java
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
                    msg = "Tipo incompatible: se esperaba string, linea " + linea;
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