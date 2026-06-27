package com.golite.reports;

public class Graphviz {

    private int count = 0;
    private final StringBuilder body = new StringBuilder();

    public String addNode(String label) {
        String id = "N" + count++;
        body.append("  ").append(id)
                .append(" [label=\"").append(label.replace("\"", "'")).append("\"];")
                .append("\n");
        return id;
    }

    public void addEdge(String a, String b) {
        body.append("  ").append(a).append(" -- ").append(b).append(";\n");
    }

    public String getDot() {
        return "graph AST {\n  node [shape=none];\n" + body + "}\n";
    }
}