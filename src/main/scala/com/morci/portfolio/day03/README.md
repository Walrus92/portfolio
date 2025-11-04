---

---

#  Día 3 – Joins, deduplicación y optimización en Spark

Hoy exploramos los **joins en Spark**, una herramienta fundamental para combinar datasets en Big Data. Además, aplicamos técnicas de optimización como **broadcast join**, y tratamos conceptos clave como la normalización y deduplicación de datos.

## 🧠 Conceptos clave

### 🔗 Tipos de joins

* **Inner Join**: Devuelve solo las filas que coinciden en ambos DataFrames.
* **Left Join / Right Join**: Devuelve todas las filas del lado izquierdo (o derecho), completando con `null` si no hay coincidencia.
* **Outer Join (Full Join)**: Devuelve todas las filas de ambos lados, uniendo por coincidencias si existen.
* **Left Anti Join**: Devuelve solo las filas del izquierdo que **no** tienen match en el derecho.
* **Semi Join**: Como un `inner join` pero sin añadir las columnas del lado derecho.
* **Cross Join**: Hace el **producto cartesiano**. Muy peligroso si no lo necesitas.

---

### ❌ ¿Qué es el producto cartesiano?

Un **producto cartesiano** es cuando cada fila del DataFrame izquierdo se combina con **todas** las filas del derecho.

#### Ejemplo:

Si tienes:

* DF1 con 3 filas
* DF2 con 2 filas

El resultado tendrá 3×2 = **6 filas**.

Es costoso y se usa **solo si sabes lo que haces**, por ejemplo, para generar combinaciones posibles o simulaciones.

**Evítalo** si puedes. Si haces un `join` sin condición (`df1.join(df2)`), Spark puede hacer uno sin darte cuenta.

---

### 🚀 Broadcast Join

En Spark, un **broadcast join** es una técnica para **evitar el shuffle de datos** cuando uno de los DataFrames es muy pequeño. Spark lo envía a todos los workers.

#### Cuándo usarlo:

* Cuando un DataFrame es **muy pequeño** (regla práctica: < 10 MB)
* Cuando sabes que ese DataFrame **no crece** inesperadamente

#### ¿Cómo forzarlo?

```scala
import org.apache.spark.sql.functions.broadcast

val result = broadcast(smallDF).join(largeDF, "user_id")
```

Esto fuerza a Spark a replicar `smallDF` en todos los nodos para evitar la costosa redistribución (`shuffle`) de `largeDF`.

#### ¿Cómo asegurarte de que es pequeño?

Puedes hacer:

```scala
println(s"Size: " + smallDF.count())
```

O incluso estimar el tamaño:

```scala
val approxSize = smallDF.rdd.map(_.toString().getBytes.length.toLong).reduce(_ + _)
println(s"Approx size in bytes: $approxSize")
```

Si el `count()` es muy bajo (< 1.000 filas) o el tamaño estimado es < 10 MB, es buen candidato para broadcast.

---

### 🧹 Limpieza y normalización

* `dropDuplicates()` para evitar repeticiones antes de joins
* `trim()` y `lower()` para normalizar campos tipo string
* Uso de `groupBy` y `agg` para sumarizar y ordenar datos

---

## 💡 Reto práctico del día

Teníamos dos ficheros:

* `users.json`: contiene usuarios con `user_id`, `name`, `age`, `country`
* `purchases.json`: contiene compras con `purchase_id`, `user_id`, `product`, `amount`, `date`

Se han realizado:

* ✅ Un `inner join` para combinar usuarios y compras
* ✅ Un `left anti join` para detectar usuarios sin compras
* ✅ Una agregación del **total gastado por usuario**, ordenando por importe
* ✅ Una normalización del campo `country`
* ✅ Un `broadcast join` forzado
* ✅ Guardado del resultado en CSV

---

## 📁 Estructura recomendada del proyecto

```
src/
  main/
    scala/
      com/
        morci/
          portfolio/
            Day03_Joins.scala
    resources/
      users.json
      purchases.json
day03/
  output/
    resultado.csv
```

---

## 📝 Conclusión

* Aprender a elegir bien el tipo de join es esencial en procesamiento de datos.
* Broadcast puede mejorar radicalmente el rendimiento si se aplica correctamente.
* El producto cartesiano es peligroso: revísalo si tu `join` explota.
* Unos pocos pasos de limpieza mejoran mucho la estabilidad del pipeline.

---
