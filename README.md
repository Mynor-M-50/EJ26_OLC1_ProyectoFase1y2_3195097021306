# Interprete GoLite - Fase 1

## Descripcion

Interprete del lenguaje GoLite implementado en Java, desarrollado como proyecto del curso Organizacion de Lenguajes y Compiladores 1. GoLite es un subconjunto del lenguaje Go que cubre los conceptos fundamentales de compiladores.

El interprete esta construido en tres fases:

- **Analisis Lexico** con JFlex: tokenizacion del codigo fuente
- **Analisis Sintactico** con CUP: parser LALR(1) que construye el AST
- **Analisis Semantico + Ejecucion**: recorrido del AST con manejo de scopes

## Caracteristicas - Fase 1

- Tipos de datos primitivos: `int`, `float64`, `string`, `bool`, `rune`
- Declaracion de variables con tipo explicito (`var x int = 10`) e inferencia de tipo (`x := 10`)
- Operadores aritmeticos, logicos y de comparacion con precedencia correcta
- Sentencias `if`, `else if`, `else` con bloques obligatorios
- Bucles `for` estilo while y estilo clasico (init; cond; update)
- `break` y `continue` dentro de bucles
- Bloques anonimos con su propio ambito (scope anidado)
- Funciones embebidas: `fmt.Println`, `strconv.Atoi`, `strconv.ParseFloat`, `reflect.TypeOf`
- Reporte de tokens reconocidos (tabla con lexema, tipo, linea, columna)
- Reporte de errores lexicos, sintacticos y semanticos
- Interfaz grafica en Java Swing con editor, consola de salida y reportes

## Documentacion

- [Manual de Usuario](ManualUsuario_GoLite_Fase1.pdf)
- [Manual Tecnico](ManualTecnico_GoLite_Fase1.pdf)

## Requisitos Previos

- **Java JDK:** version 17 o superior
- **Apache Maven:** version 3.9.x o superior (solo si se compila desde fuente)
- **Sistema Operativo:** Ubuntu Linux (recomendado) o cualquier sistema con soporte Java

No se requieren dependencias adicionales. El JAR ejecutable incluye todas las librerias necesarias.

## Ejecucion (JAR ejecutable)

Descargar el JAR desde la seccion Releases del repositorio y ejecutar:

```bash
java -jar GoLiteInterpreter-1.0-SNAPSHOT.jar
```

## Compilacion desde el codigo fuente

### Con Maven (recomendado)

Descargar el codigo fuente desde la seccion **Releases** del repositorio, descomprimir y ejecutar:

```bash
cd GoLiteInterpreter
 
# Compilar y empaquetar
mvn clean package
 
# Ejecutar
java -jar target/GoLiteInterpreter-1.0-SNAPSHOT.jar
```

> **Importante:** El comando `mvn` debe ejecutarse siempre desde dentro de la carpeta `GoLiteInterpreter/`, no desde la raiz del repositorio.

## Formato de archivo GoLite

El interprete acepta archivos con extension `.glt`. Ejemplo de programa valido:

```go
func main() {
    var nombre string = "GoLite"
    version := 1

    fmt.Println("Interprete:", nombre, "Version:", version)

    for i := 1; i <= 5; i++ {
        if i == 3 {
            fmt.Println("Mitad del recorrido")
            continue
        }
        fmt.Println("Iteracion:", i)
    }
}
```

## Estructura del Proyecto

```
GoLiteInterpreter/
  src/main/
    jflex/
      lexer.flex              -> Definicion del analizador lexico
    cup/
      parser.cup              -> Gramatica LALR(1) y construccion del AST
    java/com/golite/
      lexer/                  -> Lexer.java (generado por JFlex)
      parser/                 -> parser.java + Sym.java (generados por CUP)
      ast/
        NodoAST.java          -> Clase base abstracta
        expresiones/          -> Nodos de expresiones (literales, operadores)
        sentencias/           -> Nodos de sentencias (if, for, declaracion)
      interpreter/
        Interprete.java       -> Singleton principal de ejecucion
        Entorno.java          -> Manejo de scopes (pila de HashMaps)
        ErrorSemantic.java    -> Modelo de error
      gui/
        VentanaPrincipal.java -> Interfaz grafica Swing
  pom.xml                     -> Configuracion Maven
```

## Herramientas utilizadas

| Herramienta   | Version        | Uso                                      |
|---------------|----------------|------------------------------------------|
| Java JDK      | 17+            | Lenguaje de implementacion               |
| JFlex         | 1.9.1          | Generador del analizador lexico          |
| CUP           | 11b-20160615   | Generador del parser LALR(1)             |
| Apache Maven  | 3.9.x          | Compilacion y empaquetado                |
| Java Swing    | (incluido JDK) | Interfaz grafica                         |
| IntelliJ IDEA | Community Ed.  | IDE de desarrollo                        |
| Ubuntu        | Linux          | Sistema operativo de desarrollo          |

## Estudiante

**Mynor Miguel Monzon Martinez**  
Carne: 3195097021306  
Curso: Organizacion de Lenguajes y Compiladores 1  
Escuela de Vacaciones - Junio 2026  
Universidad de San Carlos de Guatemala