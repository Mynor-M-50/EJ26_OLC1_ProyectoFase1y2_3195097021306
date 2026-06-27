package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Entorno;

public class NodoAsignacion extends NodoAST {
    public String nombre;
    public String operador; // "=", "+=", "-="
    public NodoAST valor;

    public NodoAsignacion(String nombre, String operador, NodoAST valor, int linea, int columna) {
        super(linea, columna);
        this.nombre    = nombre;
        this.operador  = operador;
        this.valor     = valor;
    }

    @Override
    public Object interpretar() {
        Object nuevoValor = valor.interpretar();
        Object valorActual = Entorno.getInstancia().obtener(nombre, linea, columna);

        switch (operador) {
            case "=":
                Entorno.getInstancia().asignar(nombre, nuevoValor, linea, columna);
                break;
            case "+=":
                if (valorActual instanceof Integer && nuevoValor instanceof Integer)
                    Entorno.getInstancia().asignar(nombre, (Integer) valorActual + (Integer) nuevoValor, linea, columna);
                else if (valorActual instanceof Double || nuevoValor instanceof Double) {
                    double a = valorActual instanceof Integer ? ((Integer) valorActual).doubleValue() : (Double) valorActual;
                    double b = nuevoValor instanceof Integer ? ((Integer) nuevoValor).doubleValue() : (Double) nuevoValor;
                    Entorno.getInstancia().asignar(nombre, a + b, linea, columna);
                } else if (valorActual instanceof String && nuevoValor instanceof String)
                    Entorno.getInstancia().asignar(nombre, (String) valorActual + (String) nuevoValor, linea, columna);
                else
                    throw new RuntimeException("Operación += invalida, lionea " + linea);
                break;
            case "-=":
                if (valorActual instanceof Integer && nuevoValor instanceof Integer)
                    Entorno.getInstancia().asignar(nombre, (Integer) valorActual - (Integer) nuevoValor, linea, columna);
                else if (valorActual instanceof Double || nuevoValor instanceof Double) {
                    double a = valorActual instanceof Integer ? ((Integer) valorActual).doubleValue() : (Double) valorActual;
                    double b = nuevoValor instanceof Integer ? ((Integer) nuevoValor).doubleValue() : (Double) nuevoValor;
                    Entorno.getInstancia().asignar(nombre, a - b, linea, columna);
                } else
                    throw new RuntimeException("Operacion -= invalida, linea " + linea);
                break;
            case "*=":
                if (valorActual instanceof Integer && nuevoValor instanceof Integer)
                    Entorno.getInstancia().asignar(nombre, (Integer) valorActual * (Integer) nuevoValor, linea, columna);
                else if (valorActual instanceof Double || nuevoValor instanceof Double) {
                    double a = valorActual instanceof Integer ? ((Integer) valorActual).doubleValue() : (Double) valorActual;
                    double b = nuevoValor instanceof Integer ? ((Integer) nuevoValor).doubleValue() : (Double) nuevoValor;
                    Entorno.getInstancia().asignar(nombre, a * b, linea, columna);
                } else
                    throw new RuntimeException("Operacion *= invalida, linea " + linea);
                break;
            case "/=":
                if (valorActual instanceof Integer && nuevoValor instanceof Integer)
                    Entorno.getInstancia().asignar(nombre, (Integer) valorActual / (Integer) nuevoValor, linea, columna);
                else if (valorActual instanceof Double || nuevoValor instanceof Double) {
                    double a = valorActual instanceof Integer ? ((Integer) valorActual).doubleValue() : (Double) valorActual;
                    double b = nuevoValor instanceof Integer ? ((Integer) nuevoValor).doubleValue() : (Double) nuevoValor;
                    Entorno.getInstancia().asignar(nombre, a / b, linea, columna);
                } else
                    throw new RuntimeException("Operacion /= invalida, linea " + linea);
                break;
        }
        return null;
    }
}