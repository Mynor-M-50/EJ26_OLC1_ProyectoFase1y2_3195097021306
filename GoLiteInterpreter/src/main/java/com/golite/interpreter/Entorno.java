package com.golite.interpreter;

import com.golite.ast.sentencias.NodoFuncion;
import com.golite.ast.sentencias.NodoFuncionStruct;
import com.golite.ast.sentencias.NodoStruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entorno {

    private static Entorno instancia;

    private static final Map<String, NodoFuncion> funciones         = new HashMap<>();
    private static final Map<String, NodoStruct>  structs           = new HashMap<>();
    private static final Map<String, Map<String, NodoFuncionStruct>> metodos = new HashMap<>();

    // --- Funciones ---
    public static void registrarFuncion(String nombre, NodoFuncion funcion) {
        if (funciones.containsKey(nombre)) {
            String msg = "Funcion '" + nombre + "' ya declarada, linea " + funcion.linea;
            Interprete.getInstancia().agregarError(msg, funcion.linea, funcion.columna, "semantico");
            throw new RuntimeException(msg);
        }
        funciones.put(nombre, funcion);
    }

    public static NodoFuncion obtenerFuncion(String nombre, int linea, int columna) {
        if (funciones.containsKey(nombre))
            return funciones.get(nombre);
        String msg = "Funcion '" + nombre + "' no declarada, linea " + linea;
        Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
        throw new RuntimeException(msg);
    }

    public static Map<String, NodoFuncion> getFunciones() { return funciones; }

    // --- Structs ---
    public static void registrarStruct(String nombre, NodoStruct struct) {
        if (structs.containsKey(nombre)) {
            String msg = "Struct '" + nombre + "' ya declarado, linea " + struct.linea;
            Interprete.getInstancia().agregarError(msg, struct.linea, struct.columna, "semantico");
            throw new RuntimeException(msg);
        }
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

    public static Map<String, NodoStruct> getStructs() { return structs; }

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
            instancia = new Entorno(null, "Global");
        return instancia;
    }

    public static void resetear() {
        instancia = new Entorno(null, "Global");
        funciones.clear();
        structs.clear();
        metodos.clear();
    }

    // --- Variables ---
    public static class Variable {
        public String tipo;
        public Object valor;
        public int linea;
        public int columna;
        public String ambito;

        Variable(String tipo, Object valor, int linea, int columna, String ambito) {
            this.tipo    = tipo;
            this.valor   = valor;
            this.linea   = linea;
            this.columna = columna;
            this.ambito  = ambito;
        }
    }

    private final Map<String, Variable> tabla;
    private final Entorno padre;
    public final String ambito;

    public Entorno(Entorno padre, String ambito) {
        this.tabla  = new HashMap<>();
        this.padre  = padre;
        this.ambito = ambito != null ? ambito : (padre != null ? padre.ambito : "Global");
    }

    public void declarar(String nombre, String tipo, Object valor, int linea, int columna) {
        if (tabla.containsKey(nombre)) {
            String msg = "Variable '" + nombre + "' ya declarada en este ambito, linea " + linea;
            Interprete.getInstancia().agregarError(msg, linea, columna, "semantico");
            throw new RuntimeException(msg);
        }
        tabla.put(nombre, new Variable(tipo, valor, linea, columna, ambito));
    }

    public void declararStruct(String nombre, java.util.Map<String, Object> instancia, int linea, int columna) {
        String tipo = (String) instancia.get("__tipo__");
        tabla.put(nombre, new Variable(tipo, instancia, linea, columna, ambito));
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
        if (existeStruct(tipo)) return;
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

    // Recolectar TODAS las variables de todos los scopes para la tabla de simbolos
    public List<Variable> obtenerTodasVariables() {
        List<Variable> lista = new ArrayList<>();
        for (Map.Entry<String, Variable> e : tabla.entrySet()) {
            Variable v = e.getValue();
            Variable copia = new Variable(v.tipo, v.valor, v.linea, v.columna, v.ambito);
            // Guardar el nombre en un campo extra via subclase no es necesario
            // lo manejamos en GeneradorSimbolos con el nombre como clave
            lista.add(v);
        }
        if (padre != null)
            lista.addAll(padre.obtenerTodasVariables());
        return lista;
    }

    // Para tabla de simbolos necesitamos nombre + variable
    public void recolectarVariables(Map<String, Variable> destino) {
        for (Map.Entry<String, Variable> e : tabla.entrySet())
            destino.putIfAbsent(e.getKey(), e.getValue());
        if (padre != null)
            padre.recolectarVariables(destino);
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
        instancia = new Entorno(instancia, instancia != null ? instancia.ambito : "Global");
    }

    public static void pushBloqueConAmbito(String nuevoAmbito) {
        instancia = new Entorno(instancia, nuevoAmbito);
    }

    public static void popBloque() {
        if (instancia.padre != null)
            instancia = instancia.padre;
    }
}