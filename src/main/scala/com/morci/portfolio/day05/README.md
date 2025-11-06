## 📘 DÍA 5 — Agregaciones y funciones de ventana en Spark

### 🎯 Objetivo general

Comprender cómo Spark ejecuta **agregaciones por grupo (`groupBy`)** y **funciones de ventana (`window functions`)**, cuándo usar cada una, y cómo aplicarlas para resolver problemas típicos de análisis y procesamiento secuencial de datos. Estos conceptos son esenciales tanto en entornos reales (pipelines ETL, KPIs, logs de usuarios…) como en **entrevistas técnicas**.

---

## 🧠 1. Agregaciones con `groupBy` y `agg`

El método `groupBy` permite **agrupar filas** que comparten un mismo valor en una o varias columnas y aplicar funciones agregadas como `sum`, `count`, `avg`, `max`, `min`, etc.

**Ejemplo básico:**

```scala
val df = Seq(
  ("Lucía", "ES", 100),
  ("Lucía", "ES", 200),
  ("Miguel", "PT", 80)
).toDF("name", "country", "amount")

val grouped = df.groupBy("country").agg(
  sum("amount").alias("total_sales"),
  count("*").alias("num_records")
)

grouped.show()
```

**Salida (tabla conceptual):**
country | total_sales | num_records
ES | 300 | 2
PT | 80 | 1

**Idea clave:** `groupBy` **reduce filas**: si había 10.000 filas y agrupas por “cliente”, el resultado tendrá 1 fila por cliente.

---

## ⚙️ 2. Agregaciones múltiples y expresivas

Puedes aplicar varias funciones de agregación en una sola operación:

```scala
df.groupBy("country").agg(
  round(avg("amount"), 2).alias("avg_sales"),
  max("amount").alias("max_sale"),
  min("amount").alias("min_sale")
)
```

Agregaciones **condicionales** con `when` dentro de `agg`:

```scala
df.groupBy("country").agg(
  sum(when(col("amount") > 100, 1).otherwise(0)).alias("high_value_sales")
)
```

---

## 🧱 3. Funciones de ventana (window functions)

A diferencia de `groupBy`, las **funciones de ventana no reducen el número de filas**; añaden **columnas calculadas** basadas en otras filas “relacionadas” (por grupo, por orden, etc.).

**Imports necesarios:**

```scala
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
```

---

## 📐 4. Estructura de una ventana

```scala
val w = Window.partitionBy("columna_grupo").orderBy("columna_orden")
```

* **partitionBy**: define **el grupo** (cliente, país, etc.)
* **orderBy**: define **el orden** dentro del grupo (fecha, importe, etc.)
* Aplicas funciones con `.over(w)`.

---

## 🔍 5. Ejemplo básico de ventana

```scala
val sales = Seq(
  ("Lucía", "2023-01-01", 100),
  ("Lucía", "2023-01-05", 50),
  ("Lucía", "2023-01-10", 120),
  ("Miguel", "2023-01-02", 80),
  ("Miguel", "2023-01-09", 150)
).toDF("customer", "date", "amount")

val w = Window.partitionBy("customer").orderBy("date")

val withRunningTotal = sales.withColumn("running_total", sum("amount").over(w))
```

**Resultado (tabla conceptual):**
customer | date | amount | running_total
Lucía | 2023-01-01 | 100 | 100
Lucía | 2023-01-05 | 50  | 150
Lucía | 2023-01-10 | 120 | 270
Miguel | 2023-01-02 | 80  | 80
Miguel | 2023-01-09 | 150 | 230

**Idea clave:** el número de filas **no cambia**, pero cada fila “sabe lo que pasó antes”.

---

## 🔢 6. Funciones comunes de ventana

| Función               | Descripción                       | Ejemplo                     |
| --------------------- | --------------------------------- | --------------------------- |
| `row_number()`        | Numera las filas dentro del grupo | `row_number().over(w)`      |
| `rank()`              | Ranking con huecos por empates    | `rank().over(w)`            |
| `dense_rank()`        | Ranking sin huecos                | `dense_rank().over(w)`      |
| `lag(col, n)`         | Valor anterior de `col`           | `lag("amount", 1).over(w)`  |
| `lead(col, n)`        | Valor siguiente de `col`          | `lead("amount", 1).over(w)` |
| `sum()/avg()/count()` | Acumulados por ventana            | `sum("amount").over(w)`     |

---

## 🧩 7. Comparación `groupBy` vs `window`

| Aspecto           | `groupBy`             | `window`                          |
| ----------------- | --------------------- | --------------------------------- |
| Agrupa datos      | ✅ Sí                  | 🚫 No                             |
| Reduce filas      | ✅ Sí                  | 🚫 No                             |
| Mantiene columnas | 🚫 No                 | ✅ Sí                              |
| Se usa para       | Totales y resúmenes   | Rankings, acumulados, diferencias |
| Ejemplo           | Total ventas por país | Ventas acumuladas por cliente     |

---

## 💬 8. Ejemplo con `lag`, `lead` y `row_number`

```scala
val w = Window.partitionBy("customer").orderBy("date")

val windowed = sales
  .withColumn("purchase_num", row_number().over(w))
  .withColumn("diff_vs_prev", col("amount") - lag("amount", 1).over(w))
  .withColumn("next_amount", lead("amount", 1).over(w))
  .withColumn("avg_until_now", round(avg("amount").over(w), 2))

windowed.show()
```

**Resultado (tabla conceptual):**

| customer | date       | amount | purchase_num | diff_vs_prev | next_amount | avg_until_now |
| -------- | ---------- | ------ | ------------ | ------------ | ----------- | ------------- |
| Lucía    | 2023-01-01 | 100    | 1            | null         | 50          | 100.0         |
| Lucía    | 2023-01-05 | 50     | 2            | -50          | 120         | 75.0          |
| Lucía    | 2023-01-10 | 120    | 3            | 70           | null        | 90.0          |
| Miguel   | 2023-01-02 | 80     | 1            | null         | 150         | 80.0          |
| Miguel   | 2023-01-09 | 150    | 2            | 70           | null        | 115.0         |

---

## 🧠 9. Conclusiones teóricas

* **`groupBy`**: reduce datos; ideal para totales, sumas, medias, KPIs.
* **`window`**: mantiene todas las filas; ideal para acumulados, rankings, diferencias.
* Las funciones ventana son **muy potentes** en Spark SQL.
* Combinadas con `when`, `lag`, `lead` y `rank`, generan transformaciones analíticas expresivas.
* En entrevistas es frecuente: “última fila por usuario”, “top N por grupo”, “running total por cliente”.

---

# 🧩 RETO DEL DÍA 5 — Ranking y acumulado de ventas

## 📁 Dataset: `sales_data.json`

Guarda en `src/main/resources/sales_data.json`:

```json
[
  {"region": "South", "salesperson": "Lucía", "date": "2023-01-01", "amount": 500},
  {"region": "South", "salesperson": "Lucía", "date": "2023-01-05", "amount": 200},
  {"region": "South", "salesperson": "Lucía", "date": "2023-01-10", "amount": 800},
  {"region": "North", "salesperson": "Miguel", "date": "2023-01-02", "amount": 300},
  {"region": "North", "salesperson": "Miguel", "date": "2023-01-09", "amount": 400},
  {"region": "North", "salesperson": "Carla", "date": "2023-01-03", "amount": 1000},
  {"region": "North", "salesperson": "Carla", "date": "2023-01-04", "amount": 200}
]
```

### 🎯 Objetivos

1. Leer el JSON `sales_data.json`.
2. Por **vendedor (`salesperson`)** calcular:

    * **Número de venta** (`row_number`)
    * **Acumulado de ventas** (`sum.over(window)`)
    * **Media acumulada** (`avg.over(window)`)
    * **Diferencia con la venta anterior** (`lag`)
3. Calcular el **total de ventas por región** (`groupBy` + `agg`).
4. **Unir** ambos resultados en un único DataFrame (`join`).
5. **Escribir** el resultado final en CSV en `src/main/resources/day05_output`.

### 💬 Extra (nivel entrevista)

* Ranking global de vendedores por ventas totales (`rank().over(Window.orderBy(desc("total_sales")))`).
* Top 2 vendedores por región.
