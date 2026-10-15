
package org.karel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.TaskCounter;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;

import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class WC_Runner {

    public static void main(String[] args) throws Exception {

        // Validar los argumentos recibidos.
        if (args.length != 2) {
            System.err.println(
                    "Uso: WC_Runner <entrada_hdfs> <salida_hdfs>"
            );
            System.exit(2);
        }

        Configuration conf = new Configuration();

        Path inputPath = new Path(args[0]);
        Path outputPath = new Path(args[1]);

        // Comprobar la existencia de la entrada.
        FileSystem inputFS = inputPath.getFileSystem(conf);

        if (!inputFS.exists(inputPath)) {
            System.err.println(
                    "ERROR: La ruta de entrada no existe: " + inputPath
            );
            System.exit(2);
        }

        // Evitar sobrescribir resultados anteriores.
        FileSystem outputFS = outputPath.getFileSystem(conf);

        if (outputFS.exists(outputPath)) {
            System.err.println(
                    "ERROR: La ruta de salida ya existe: " + outputPath
            );
            System.exit(2);
        }

        // Crear el trabajo MapReduce.
        Job job = Job.getInstance(
                conf,
                "WordCount - El Conde de Montecristo"
        );

        job.setJarByClass(WC_Runner.class);

        // Configuracion de las clases.
        job.setMapperClass(WC_Mapper.class);
        job.setCombinerClass(WC_Reducer.class);
        job.setReducerClass(WC_Reducer.class);

        // Tipos de salida del Mapper.
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        // Tipos de salida del Reducer.
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // Formatos de entrada y salida.
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        // Un solo Reducer para generar un archivo final.
        job.setNumReduceTasks(1);

        // Configurar rutas HDFS.
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        // Ejecutar MapReduce y esperar su finalizacion.
        boolean success = job.waitForCompletion(true);

        if (success) {

            // Obtener estadisticas de ejecucion.
            long totalWords = job.getCounters()
                    .findCounter(TaskCounter.MAP_OUTPUT_RECORDS)
                    .getValue();

            long uniqueWords = job.getCounters()
                    .findCounter(TaskCounter.REDUCE_INPUT_GROUPS)
                    .getValue();

            System.out.println();
            System.out.println("====================================");
            System.out.println("       RESULTADOS DE WORDCOUNT       ");
            System.out.println("====================================");

            System.out.println(
                    "Libro: El Conde de Montecristo"
            );

            System.out.println(
                    "Total de palabras: " + totalWords
            );

            System.out.println(
                    "Palabras diferentes: " + uniqueWords
            );

            System.out.println(
                    "Directorio de salida: " + outputPath
            );

            System.out.println(
                    "Estado: EJECUCION EXITOSA"
            );

            System.out.println("====================================");

        } else {

            System.err.println(
                    "ERROR: El procesamiento MapReduce fallo."
            );
        }

        // Informar el resultado al sistema operativo.
        System.exit(success ? 0 : 1);
    }
}
