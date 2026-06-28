package com.golite.ast.sentencias;

import com.golite.ast.NodoAST;
import com.golite.interpreter.Interprete;
import java.util.ArrayList;
import java.util.List;

public class NodoPrograma extends NodoAST {
    public List<NodoAST> funciones;
    public List<NodoAST> sentencias;
    public List<NodoAST> sentenciasGlobales;

    public NodoPrograma(List<NodoAST> topList, List<NodoAST> sentencias, List<NodoAST> ignorado) {
        super(0, 0);
        this.sentencias = sentencias;
        this.funciones = new ArrayList<>();
        this.sentenciasGlobales = new ArrayList<>();
        for (NodoAST nodo : topList) {
            if (nodo == null) continue;
            if (nodo instanceof NodoFuncion
                    || nodo instanceof NodoStruct
                    || nodo instanceof NodoFuncionStruct) {
                funciones.add(nodo);
            } else {
                sentenciasGlobales.add(nodo);
            }
        }
    }

    @Override
    public Object interpretar() {
        // 1. Registrar funciones y structs
        for (NodoAST f : funciones) {
            if (f == null) continue;
            try {
                f.interpretar();
            } catch (RuntimeException e) {
                String msg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
                boolean yaRegistrado = Interprete.getInstancia().getErrores().stream()
                        .anyMatch(err -> err.descripcion.equals(msg));
                if (!yaRegistrado)
                    Interprete.getInstancia().agregarError(msg, 0, 0, "semantico");
            }
        }

        // 2. Ejecutar sentencias globales (antes del main)
        for (NodoAST sg : sentenciasGlobales) {
            if (sg == null) continue;
            try {
                sg.interpretar();
            } catch (ReturnException e) {
                break;
            } catch (RuntimeException e) {
                String msg = e.getMessage() != null ? e.getMessage() : "Error desconocido";
                boolean yaRegistrado = Interprete.getInstancia().getErrores().stream()
                        .anyMatch(err -> err.descripcion.equals(msg));
                if (!yaRegistrado)
                    Interprete.getInstancia().agregarError(msg, 0, 0, "semantico");
            }
        }

        // 3. Ejecutar el main
        for (NodoAST s : sentencias) {
            if (s == null) continue;
            try {
                s.interpretar();
            } catch (ReturnException e) {
                break;
            }
        }
        return null;
    }
}