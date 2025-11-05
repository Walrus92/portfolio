# 📘 Día 4 – Columnas complejas, estructuras anidadas y funciones avanzadas

## 🎯 Objetivo
Aprender a trabajar con columnas de tipo `struct`, `array` y `map` en Spark, accediendo a campos internos y transformándolos en un esquema tabular mediante funciones avanzadas.

---

## 🧠 Conceptos clave

### 🔹 StructType
Una estructura que contiene varios campos, como un objeto JSON.  
Acceso con `col("campo.subcampo")` o `$"campo.subcampo"`.

### 🔹 ArrayType
Columna que contiene listas.  
Manipulaciones comunes:
- `explode` → convierte cada elemento del array en una fila
- `size` → número de elementos
- `array_contains` → comprueba si contiene un valor

### 🔹 MapType
Columna que contiene pares clave/valor.  
Acceso: `col("mapa")("clave")`.

### 🔹 Funciones útiles
- `withColumn` → crear o reemplazar columnas
- `explode` → expandir arrays
- `when` / `otherwise` → condiciones
- `lower`, `trim`, `size`, `struct`, `concat_ws` → transformaciones comunes

---

## 💡 Reto práctico

Dataset: `user_events.json`  
Cada fila representa un usuario con perfil, preferencias, eventos y metadatos.

Tareas:
1. Leer el JSON con estructuras anidadas
2. Extraer campos internos (`profile.name`, `metadata.ip`, etc.)
3. Derivar columnas nuevas (`country_lower`, `num_preferences`, `is_mobile`)
4. Hacer `explode` sobre los eventos
5. Clasificar usuarios con `when` / `otherwise`
6. Guardar el resultado final en CSV

---

## 📁 Estructura de salida

src/main/resources/day04_output/
part-00000.csv

yaml
Copiar código

---

## 🧩 Conclusiones

- Spark permite manejar datos anidados directamente desde JSONs complejos.
- `explode` y `struct` son esenciales para “normalizar” información jerárquica.
- La evaluación perezosa sigue aplicando: nada se ejecuta hasta que hay una acción (`write`, `show`, etc.).
- Este tipo de transformaciones aparecen constantemente en pipelines reales de streaming, logs o datos de APIs.
