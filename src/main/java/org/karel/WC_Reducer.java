
package org.karel;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class WC_Reducer
        extends Reducer<Text, LongWritable, Text, LongWritable> {

    private final LongWritable result = new LongWritable();

    @Override
    protected void reduce(
            Text key,
            Iterable<LongWritable> values,
            Context context
    ) throws IOException, InterruptedException {

        long total = 0L;

        // Sumar todas las apariciones de la palabra.
        for (LongWritable value : values) {
            total += value.get();
        }

        // Emitir (palabra, frecuencia_total).
        result.set(total);
        context.write(key, result);
    }
}
