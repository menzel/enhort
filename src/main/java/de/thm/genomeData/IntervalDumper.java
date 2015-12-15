package de.thm.genomeData;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;

/**
 * Created by Michael Menzel on 15/12/15.
 */
public class IntervalDumper {
    private Path baseDir;
    private Kryo kryo;

    public IntervalDumper(Path baseDir) {
        this.baseDir = baseDir;
        kryo = new Kryo();
    }

    public void dumpInterval(IntervalNamed interval, String name){
        try (Output output = new Output(new FileOutputStream(baseDir.resolve(name).toString()))) {

            kryo.writeObject(output, interval);
            output.flush();
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public IntervalNamed getInterval(File file){
        IntervalNamed interval;

        try {
            Input input = new Input(new FileInputStream(file.toString()));
            interval = kryo.readObject(input, IntervalNamed.class);

            input.close();
            return interval;

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }
}
