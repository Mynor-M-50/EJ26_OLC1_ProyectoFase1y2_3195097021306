package com.golite.lexer;

import com.golite.parser.*;
import java_cup.runtime.Symbol;

%%

%class Lexer
%unicode
%cupsym com.golite.parser.Sym
%cup
%line
%column
%public

%{
    public int getLine() { return yyline + 1; }
    public int getColumn() { return yycolumn + 1; }
%}

/* Definiciones */
LineTerminator  = \r|\n|\r\n
WhiteSpace      = {LineTerminator} | [ \t\f]
Digit           = [0-9]
Letter          = [a-zA-Z_]
Identifier      = {Letter}({Letter}|{Digit})*
IntLiteral      = {Digit}+
FloatLiteral    = {Digit}+"."{Digit}+
StringLiteral   = \"([^\"\\\n]|\\.)*\"
RuneLiteral     = \'([^\'\\\n]|\\.)\'

%%

/* Palabras reservadas */
"var"           { return new Symbol(Sym.VAR,       yyline+1, yycolumn+1); }
"func"          { return new Symbol(Sym.FUNC,      yyline+1, yycolumn+1); }
"main"          { return new Symbol(Sym.MAIN,      yyline+1, yycolumn+1); }
"if"            { return new Symbol(Sym.IF,        yyline+1, yycolumn+1); }
"else"          { return new Symbol(Sym.ELSE,      yyline+1, yycolumn+1); }
"for"           { return new Symbol(Sym.FOR,       yyline+1, yycolumn+1); }
"break"         { return new Symbol(Sym.BREAK,     yyline+1, yycolumn+1); }
"continue"      { return new Symbol(Sym.CONTINUE,  yyline+1, yycolumn+1); }
"return"        { return new Symbol(Sym.RETURN,    yyline+1, yycolumn+1); }
"true"          { return new Symbol(Sym.TRUE,      yyline+1, yycolumn+1, true); }
"false"         { return new Symbol(Sym.FALSE,     yyline+1, yycolumn+1, false); }
"nil"           { return new Symbol(Sym.NIL,       yyline+1, yycolumn+1); }
"int"           { return new Symbol(Sym.TINT,      yyline+1, yycolumn+1); }
"float64"       { return new Symbol(Sym.TFLOAT64,  yyline+1, yycolumn+1); }
"string"        { return new Symbol(Sym.TSTRING,   yyline+1, yycolumn+1); }
"bool"          { return new Symbol(Sym.TBOOL,     yyline+1, yycolumn+1); }
"rune"          { return new Symbol(Sym.TRUNE,     yyline+1, yycolumn+1); }
"fmt.Println"   { return new Symbol(Sym.PRINTLN,   yyline+1, yycolumn+1); }
"strconv.Atoi"  { return new Symbol(Sym.ATOI,      yyline+1, yycolumn+1); }
"strconv.ParseFloat" { return new Symbol(Sym.PARSEFLOAT, yyline+1, yycolumn+1); }
"reflect.TypeOf" { return new Symbol(Sym.TYPEOF,   yyline+1, yycolumn+1); }

/* Operadores */
":="            { return new Symbol(Sym.DECL_ASSIGN, yyline+1, yycolumn+1); }
"=="            { return new Symbol(Sym.EQ,        yyline+1, yycolumn+1); }
"!="            { return new Symbol(Sym.NEQ,       yyline+1, yycolumn+1); }
"<="            { return new Symbol(Sym.LEQ,       yyline+1, yycolumn+1); }
">="            { return new Symbol(Sym.GEQ,       yyline+1, yycolumn+1); }
"&&"            { return new Symbol(Sym.AND,       yyline+1, yycolumn+1); }
"||"            { return new Symbol(Sym.OR,        yyline+1, yycolumn+1); }
"+="            { return new Symbol(Sym.PLUS_ASSIGN,  yyline+1, yycolumn+1); }
"-="            { return new Symbol(Sym.MINUS_ASSIGN, yyline+1, yycolumn+1); }
"+"             { return new Symbol(Sym.PLUS,      yyline+1, yycolumn+1); }
"-"             { return new Symbol(Sym.MINUS,     yyline+1, yycolumn+1); }
"*"             { return new Symbol(Sym.TIMES,     yyline+1, yycolumn+1); }
"/"             { return new Symbol(Sym.DIV,       yyline+1, yycolumn+1); }
"%"             { return new Symbol(Sym.MOD,       yyline+1, yycolumn+1); }
"="             { return new Symbol(Sym.ASSIGN,    yyline+1, yycolumn+1); }
"<"             { return new Symbol(Sym.LT,        yyline+1, yycolumn+1); }
">"             { return new Symbol(Sym.GT,        yyline+1, yycolumn+1); }
"!"             { return new Symbol(Sym.NOT,       yyline+1, yycolumn+1); }

/* Delimitadores */
"("             { return new Symbol(Sym.LPAREN,    yyline+1, yycolumn+1); }
")"             { return new Symbol(Sym.RPAREN,    yyline+1, yycolumn+1); }
"{"             { return new Symbol(Sym.LBRACE,    yyline+1, yycolumn+1); }
"}"             { return new Symbol(Sym.RBRACE,    yyline+1, yycolumn+1); }
","             { return new Symbol(Sym.COMMA,     yyline+1, yycolumn+1); }
";"             { return new Symbol(Sym.SEMICOLON, yyline+1, yycolumn+1); }
"."             { return new Symbol(Sym.DOT,       yyline+1, yycolumn+1); }

/* Literales */
{IntLiteral}    { return new Symbol(Sym.INT_LIT,   yyline+1, yycolumn+1, Integer.parseInt(yytext())); }
{FloatLiteral}  { return new Symbol(Sym.FLOAT_LIT, yyline+1, yycolumn+1, Double.parseDouble(yytext())); }
{StringLiteral} { return new Symbol(Sym.STRING_LIT,yyline+1, yycolumn+1, yytext().substring(1, yytext().length()-1)); }
{RuneLiteral}   { return new Symbol(Sym.RUNE_LIT,  yyline+1, yycolumn+1, yytext().charAt(1)); }

/* Identificadores */
{Identifier}    { return new Symbol(Sym.ID,        yyline+1, yycolumn+1, yytext()); }

/* Comentarios */
"//"[^\n]*      { /* ignorar comentario de línea */ }
"/*"([^*]|\*+[^*/])*\*+"/" { /* ignorar comentario multilínea */ }

/* Espacios en blanco */
{WhiteSpace}    { /* ignorar */ }

/* Error léxico */
[^]             { System.err.println("Error léxico: carácter no reconocido '" + yytext() + "' en línea " + (yyline+1) + ", columna " + (yycolumn+1)); }