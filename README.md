# WordCount con Hadoop: El conde de Montecristo

Implementación de **WordCount con Java, Apache Hadoop MapReduce y HDFS** para contar la frecuencia de las palabras presentes en la novela *The Count of Monte Cristo* (*El conde de Montecristo*), obtenida de Project Gutenberg.

Este proyecto forma parte de una práctica de la asignatura **Procesamiento Masivo de Datos**.

## Objetivo

Desarrollar y ejecutar un trabajo MapReduce capaz de leer un libro almacenado en HDFS, identificar sus palabras y calcular cuántas veces aparece cada una. El proceso incluye una prueba con un archivo pequeño y una validación de los resultados obtenidos sobre el libro completo.

## Tecnologías utilizadas

- **Java 8** para la implementación de WordCount.
- **Apache Hadoop 3.5.0**, con HDFS y MapReduce.
- **Apache Maven** para administrar dependencias y compilar el proyecto.
- **IntelliJ IDEA** como entorno de desarrollo.
- **Ubuntu en WSL2** para ejecutar Hadoop desde Windows.

## Libro utilizado

- **Título:** *The Count of Monte Cristo*.
- **Autores:** Alexandre Dumas y Auguste Maquet.
- **Fuente:** [Project Gutenberg, eBook n.º 1184](https://www.gutenberg.org/ebooks/1184).
- **Archivo de entrada:** `datos/el-conde-de-montecristo.txt`.

Se utilizó el archivo TXT completo, **sin eliminar los encabezados ni la información adicional de Project Gutenberg**. Por tanto, las frecuencias corresponden a todo el archivo procesado y no exclusivamente al texto narrativo.

## Estructura del proyecto

```text
ElCondeDeMontecristo/
├── datos/
│   ├── el-conde-de-montecristo.txt
│   └── prueba_wordcount.txt
├── entrega/
│   ├── WordCount con Hadoop - El conde de Montecristo.pdf
│   └── WordCount con Hadoop - El conde de Montecristo.docx
├── evidencias/
│   ├── 01_Hadoop_Iniciado.png
│   ├── 02_Libro_Original.png
│   ├── 03_Compilacion_Maven.png
│   ├── 04_Prueba_Ejecucion.png
│   ├── 05_Prueba_Resultado.png
│   ├── 06_Carga_HDFS.png
│   ├── 07_Ejecucion_MapReduce.png
│   ├── 08_Salida_HDFS.png
│   ├── 09_Resultados_WordCount.png
│   └── 10_Archivos_Resultados.png
├── resultados/
│   ├── resultado_wordcount.txt
│   └── top_20_palabras.txt
├── src/main/java/org/karel/
│   ├── WC_Mapper.java
│   ├── WC_Reducer.java
│   └── WC_Runner.java
├── .gitignore
├── pom.xml
└── README.md
```

La carpeta `target/` se genera durante la compilación y no es necesario incluirla en el repositorio.

## Funcionamiento de WordCount

El programa utiliza tres clases principales:

1. **`WC_Mapper`**: lee las líneas del documento, identifica palabras, normaliza las mayúsculas y minúsculas y emite pares `(palabra, 1)`.
2. **`WC_Reducer`**: suma los valores asociados a cada palabra para obtener su frecuencia total. También se utiliza como *Combiner* para realizar agregaciones parciales.
3. **`WC_Runner`**: configura y ejecuta el trabajo MapReduce, define las rutas de entrada y salida y muestra las estadísticas finales.

## Compilación y ejecución

Los siguientes comandos corresponden a un entorno Ubuntu/WSL2 con Hadoop instalado y configurado. Deben ejecutarse desde la carpeta raíz del proyecto. La salida de Hadoop debe apuntar a un directorio que todavía no exista.

### 1. Iniciar y comprobar Hadoop

```bash
start-hadoop
jps
```

Se deben observar los servicios necesarios, entre ellos `NameNode`, `DataNode`, `SecondaryNameNode`, `ResourceManager` y `NodeManager`.

### 2. Compilar el proyecto

Desde IntelliJ IDEA, ejecutar **Maven > Lifecycle > package**. Alternativamente, si Maven está disponible en la terminal:

```bash
mvn clean package
```

Se genera el archivo `target/WC_count-1.0-SNAPSHOT.jar`.

### 3. Cargar el libro a HDFS

```bash
hdfs dfs -mkdir -p /user/$USER/montecristo/entrada
hdfs dfs -put -f datos/el-conde-de-montecristo.txt /user/$USER/montecristo/entrada/
hdfs dfs -ls -h /user/$USER/montecristo/entrada/
```

### 4. Ejecutar WordCount

```bash
hadoop jar target/WC_count-1.0-SNAPSHOT.jar org.karel.WC_Runner \
  /user/$USER/montecristo/entrada \
  /user/$USER/montecristo/salida
```

### 5. Consultar los resultados en HDFS

```bash
hdfs dfs -ls -h /user/$USER/montecristo/salida/
hdfs dfs -cat /user/$USER/montecristo/salida/part-r-00000 | sed -n '1,25p'
```

El archivo `part-r-00000` contiene las palabras y sus frecuencias; `_SUCCESS` indica que el trabajo se completó correctamente.

### 6. Exportar y validar los resultados

```bash
hdfs dfs -get -f /user/$USER/montecristo/salida/part-r-00000 resultados/resultado_wordcount.txt
sort -k2,2nr resultados/resultado_wordcount.txt | sed -n '1,20p' > resultados/top_20_palabras.txt
wc -l resultados/resultado_wordcount.txt
awk '{total += $2} END {print "Total de palabras:", total}' resultados/resultado_wordcount.txt
```

## Prueba previa

Antes del procesamiento definitivo se utilizó `datos/prueba_wordcount.txt` para verificar las reglas de conteo. El trabajo produjo **11 apariciones** distribuidas en **5 palabras diferentes**:

```text
cristo  2
hadoop  3
hello   2
it's    2
monte   2
```

## Resultados del libro completo

| Indicador | Resultado |
|---|---:|
| Líneas de entrada procesadas | 61.680 |
| Apariciones totales de palabras | **467.664** |
| Palabras diferentes | **16.242** |
| Estado del trabajo | Ejecución exitosa |

Las cinco palabras más frecuentes fueron:

| Palabra | Frecuencia |
|---|---:|
| `the` | 18.412 |
| `of` | 12.920 |
| `to` | 12.899 |
| `and` | 12.569 |
| `a` | 9.472 |

El listado completo se encuentra en `resultados/resultado_wordcount.txt` y las veinte palabras más frecuentes en `resultados/top_20_palabras.txt`.

## Evidencias y documentación

La carpeta `evidencias/` contiene diez capturas numeradas que documentan la inicialización de Hadoop, la compilación, la prueba, la carga a HDFS, la ejecución de MapReduce y la validación de resultados.

El informe de la práctica se encuentra en `entrega/`, disponible en formatos **PDF** y **DOCX**.

## Conclusión

La práctica permitió implementar y comprobar el funcionamiento de WordCount mediante las etapas de mapeo, combinación y reducción. El resultado final se contrastó con comandos de Linux, confirmando que los conteos obtenidos coincidían con las estadísticas reportadas por Hadoop.
