
# 📘 Día 2 – Transformaciones, Acciones y Lazy Evaluation en Spark

Este día nos centramos en comprender y practicar:

- Transformaciones vs Acciones
- Lazy Evaluation
- Cadenas de transformaciones
- Lectura y limpieza de datos reales
- Derivación de columnas
- Agrupaciones y estadísticas básicas

---

## 🧠 Conceptos clave

### 🔁 Transformaciones

Las **transformaciones** (como `filter`, `map`, `withColumn`, etc.) **no se ejecutan inmediatamente**. Devuelven un nuevo DataFrame transformado, pero no se computa hasta que se lanza una acción.

### ⚡ Acciones

Las **acciones** (como `show`, `collect`, `count`, `write`, etc.) son las que **disparan la ejecución real**. Spark ejecuta entonces el DAG de transformaciones acumuladas hasta ese punto.

### ⏳ Lazy Evaluation

Spark **aplaza** toda ejecución hasta que sea estrictamente necesaria (una acción). Esto permite optimizaciones automáticas como:

- Eliminación de pasos innecesarios
- Reordenamiento de operaciones
- Combinar transformaciones en una sola etapa de ejecución

---

## 💡 Reto práctico: Análisis de transacciones bancarias

Se ha trabajado con un archivo realista de transacciones (`transactions.json`) con esta estructura:

```json
{
  "account_number": "3570559307609536",
  "transaction_date": "11/9/2022",
  "transaction_amount": 3412.53,
  "transaction_type": "transfer",
  "transaction_description": "...",
  "transaction_category": "utilities"
}
```

---

### ✅ Transformaciones realizadas:

- Filtrado de transacciones con `transaction_amount <= 0`
- Conversión de `transaction_date` a tipo fecha
- Derivación de año y mes
- Clasificación en `tipo_movimiento` (`entrada`, `salida`, `otro`)
- Normalización de la categoría (`lower + trim`)

---

### 📊 Acciones aplicadas:

- Mostrar primeras filas (`show`)
- Conteo por tipo de movimiento
- Top 5 categorías más frecuentes
- Gasto total por mes y año
- Detección de duplicados

---

## 📁 Código fuente

- Archivo: `Day02_Reto.scala`
- Ruta: `src/main/scala/com/morci/portfolio/Day02_Reto.scala`
- Dataset: `src/main/resources/transactions.json`

---

## 🔚 Conclusiones

- ✅ Spark **no ejecuta transformaciones hasta que lanzamos una acción**
- ✅ Podemos encadenar múltiples transformaciones de forma eficiente
- ✅ Aprendimos a usar funciones como `to_date`, `year`, `month`, `trim`, `lower`, `when`, `groupBy`, `agg`
- ✅ Ejecutamos acciones como `show`, `count`, `orderBy`, etc.
- ✅ Simulamos una mini ETL realista con validación, limpieza y agregaciones

---

## 📝 Próximos pasos

En el Día 3 trabajaremos con:

- Joins entre DataFrames
- Optimización de operaciones
- Lectura múltiple de fuentes
- Más lógica condicional y estructuración de pipelines