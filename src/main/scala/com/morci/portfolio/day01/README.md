# Día 1 – Arquitectura de ejecución de Spark

## 🎯 Objetivo

Entender cómo Spark ejecuta un trabajo, desde que tú escribes `.filter(...).show()` hasta que los datos realmente se procesan. Esto implica comprender:

- La jerarquía de ejecución: Driver → Job → Stage → Task → Executor
- Qué es una transformación y qué es una acción
- Qué es un DAG y cómo Spark lo construye
- Qué es un shuffle y por qué es caro
- Qué son las particiones y cómo afectan al rendimiento
- Cómo Spark convierte código Scala en un plan físico ejecutable

## 🧱 1. Jerarquía de ejecución en Spark

Cuando lanzas una aplicación Spark (por ejemplo desde IntelliJ), se crea una estructura jerárquica de ejecución:

- **Driver**: Proceso que lanza tu código y coordina todo. Vive en tu máquina o nodo principal.
- **Job**: Se lanza cada vez que se ejecuta una **acción** (`show`, `collect`, etc.).
- **Stage**: Un job se divide en una o más etapas según si hay *shuffles* (reordenación de datos).
- **Task**: Cada stage se divide en tareas. **Cada partición de datos** corresponde a una **task**.
- **Executor**: Proceso distribuido que ejecuta las tasks. En local, simulado con hilos; en cluster, en máquinas distintas.

## 🔁 2. Transformaciones vs Acciones

### ✅ Transformaciones
- **Lazy**: no se ejecutan inmediatamente.
- Devuelven un nuevo DataFrame o RDD.
- Solo se acumulan en un **DAG lógico**.
- Ejemplos: `select`, `filter`, `withColumn`, `map`, `join`, `groupBy`.

### ✅ Acciones
- **Disparan la ejecución real**.
- Devuelven un resultado real o efecto (mostrar, escribir, contar…).
- Lanzan un **job**, que genera stages y tasks.
- Ejemplos: `show`, `collect`, `count`, `first`, `write`.

### Diferencia clave:
- Una transformación **describe lo que quieres hacer**.
- Una acción **dice: ahora hazlo**.

## 🧮 3. Particiones

Una **partición** es una porción del dataset que se procesa de forma aislada por una task.

- Cada task ejecuta su lógica sobre **una partición**.
- Puedes ver el número de particiones con `.rdd.getNumPartitions`.
- Puedes controlarlas con:
    - `.repartition(n)` – fuerza una redistribución total (shuffle)
    - `.coalesce(n)` – reduce particiones sin shuffle (cuando es posible)

## 🔄 4. DAG: Directed Acyclic Graph

El **DAG** es la representación interna que Spark construye con las transformaciones encadenadas.

1. Spark construye el **DAG lógico**
2. Lo **optimiza**
3. Lo convierte en un **DAG físico** (plan de ejecución con operadores reales)

Puedes ver el DAG en texto con `.explain()` o gráficamente en la Spark UI (`http://localhost:4040`).

## 🧠 5. Plan lógico vs plan físico

### Plan lógico:
- Describe *qué* operaciones quieres hacer (`filter`, `select`, etc.)
- Es independiente del entorno físico

### Plan físico:
- Describe *cómo* se ejecutan esas operaciones
- Incluye particiones, operadores físicos, etc.

Spark convierte el plan lógico en físico usando el **optimizador Catalyst**.

## 🌪️ 6. Shuffle

Un **shuffle** ocurre cuando Spark necesita **reordenar los datos entre particiones o nodos**.

- Es un proceso **costoso** que implica disco + red
- **Causa un corte en el DAG → nuevo stage**

### Operaciones que causan shuffle:
- `groupBy`, `join`, `distinct`, `repartition`, `orderBy`

💡 Los shuffles se deben evitar o controlar: tienen gran impacto en el rendimiento.

## 🎓 7. Cómo resumirlo en una entrevista

> ❓ “¿Qué ocurre cuando haces `df.select(...).filter(...).show()`?”

**Respuesta pro:**  
Spark construye un DAG de transformaciones lazy. Al llegar la acción `.show()`, lanza un job, lo divide en stages y tasks por partición. Cada task se ejecuta en un executor. El DAG se convierte en un plan físico mediante Catalyst, y si hay redistribución de datos, se genera un shuffle y se separan los stages.

---

Este resumen es la base conceptual de Spark. Dominarlo te permite optimizar, explicar bien en entrevistas y trabajar con código más predecible y escalable.