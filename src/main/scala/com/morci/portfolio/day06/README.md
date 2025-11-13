Perfecto, aquí tienes lo que me pediste **en dos piezas claras**:

1. **README del Día 6** — limpio, en Markdown listo para copiar-pegar.
2. **Archivo Scala del ejercicio** — completo, comentado, ejecutable en IntelliJ.

Nada más. No mezclo teoría y código.

---

# ✅ **README Día 6 — Scala avanzado I: Colecciones y programación funcional**

````markdown
# 📘 DÍA 6 — Scala avanzado I: Colecciones y programación funcional

## 🎯 Objetivo general
Comprender cómo funcionan las colecciones inmutables de Scala y cómo se aplican las transformaciones funcionales (`map`, `flatMap`, `filter`, `foldLeft`, `reduce`, etc.).  
Estas operaciones son la base del estilo funcional y del diseño interno de Spark.

---

## 🧱 1. Jerarquía de colecciones en Scala
Las colecciones inmutables más usadas son:

- **Seq** → secuencia ordenada  
- **List** → lista enlazada, inmutable  
- **Vector** → alternativa más rápida para acceso aleatorio  
- **Set** → conjunto sin duplicados  
- **Map** → pares clave-valor

Scala usa **colecciones inmutables por defecto**, lo que significa que todas las transformaciones devuelven colecciones nuevas.

---

## 🧠 2. Transformaciones puras

### `map`
Aplica una función a cada elemento.
```scala
Seq(1,2,3).map(_ * 2) // Seq(2,4,6)
````

### `filter`

Filtra elementos por condición.

```scala
Seq(1,2,3,4).filter(_ % 2 == 0) // Seq(2,4)
```

### `flatMap`

Combinación de map + flatten.

```scala
Seq("hola mundo", "adios mundo").flatMap(_.split(" "))
```

---

## ⚙️ 3. Reducciones y acumulaciones

### `reduce`

Combina elementos de dos en dos.

```scala
Seq(1,2,3).reduce(_ + _) // 6
```

⚠️ No funciona en colecciones vacías.

### `fold` y `foldLeft`

Permiten un valor inicial.

```scala
Seq(1,2,3).foldLeft(0)(_ + _) // 6
```

### Diferencia entre foldLeft y foldRight

```scala
Seq(1,2,3).foldLeft(0)(_ - _)   // -6
Seq(1,2,3).foldRight(0)(_ - _)  // 2
```

---

## 🧩 4. Funciones de orden superior

En Scala las funciones se pueden pasar como valores.

```scala
def aplicar(xs: Seq[Int], f: Int => Int): Seq[Int] = xs.map(f)
aplicar(Seq(1,2,3), _ * 2) // Seq(2,4,6)
```

También se pueden currificar:

```scala
def multiplicar(a: Int)(b: Int) = a * b
val porDos = multiplicar(2) _
porDos(5) // 10
```

---

## 💼 5. Ejemplo práctico

```scala
case class Transaction(id: Int, user: String, amount: Double, category: String)
```

### Total por usuario:

```scala
transactions.groupBy(_.user).mapValues(_.map(_.amount).sum)
```

### Promedio por categoría:

```scala
transactions.groupBy(_.category).mapValues(t => t.map(_.amount).sum / t.size)
```

### Total general con foldLeft:

```scala
transactions.foldLeft(0.0)((acc, t) => acc + t.amount)
```

---

## 🧩 6. Reto práctico del Día 6

1. Crea una lista `Seq[Transaction]` con al menos 10 transacciones.
2. Realiza:

    * Un `filter` para obtener transacciones mayores de 200€.
    * Un `groupBy` + `mapValues` para obtener gasto medio por categoría.
    * Un `foldLeft` para calcular el gasto total general.
    * Un `flatMap` para obtener una lista de usuarios que hayan gastado >100€ alguna vez.
3. Imprime los resultados.

### Extra (nivel entrevista)

Implementa una función genérica:

```scala
def sumAmounts[T <: { def amount: Double }](seq: Seq[T]): Double
```

que use `foldLeft` para sumar los importes de cualquier modelo con campo `amount`.

