
package org.karel;

import java.io.IOException;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class WC_Mapper
        extends Mapper<LongWritable, Text, Text, LongWritable> {

    private static final LongWritable ONE = new LongWritable(1);

    // Reconoce palabras Unicode y contracciones.
    private static final Pattern WORD_PATTERN = Pattern.compile(
            "\\p{L}[\\p{L}\\p{M}]*(?:'\\p{L}[\\p{L}\\p{M}]*)*"
    );

    private final Text word = new Text();

    @Override
    protected void map(
            LongWritable key,
            Text value,
            Context context
    ) throws IOException, InterruptedException {

        // Convertir a minusculas.
        String line = value.toString().toLowerCase(Locale.ROOT);

        // Normalizar caracteres Unicode.
        line = Normalizer.normalize(line, Normalizer.Form.NFC);

        // Unificar los diferentes tipos de apostrofes.
        line = line.replace("\u2019", "'")
                .replace("\u2018", "'");

        // Identificar palabras validas.
        Matcher matcher = WORD_PATTERN.matcher(line);

        while (matcher.find()) {

            String token = matcher.group();

            word.set(token);

            // Emitir (palabra, 1).
            context.write(word, ONE);
        }
    }
}
