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
    private static final String extension = "kryo";

    public IntervalDumper(Path baseDir) {
        this.baseDir = baseDir;
        kryo = new Kryo();
    }

    public static String getExtension() {
        return extension;
    }

    public void dumpInterval(Interval interval, String name){
        try (Output output = new Output(new FileOutputStream(baseDir.resolve(name).toString() + "." + extension))) {

            kryo.writeObject(output, interval);
            output.flush();
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    public Interval getInterval(File file){
        Interval interval;
        String name = file.getName();
        name = name.substring(0,name.indexOf(".")) + ".kryo";

        try {
            Input input = new Input(new FileInputStream(baseDir + "/kryo/" + name));
            interval = kryo.readObject(input, Interval.class);

            input.close();
            return interval;

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }

    public boolean exists(String name) {
        name = name.substring(0,name.indexOf(".")) + ".kryo";
        Path path = baseDir.resolve("kryo");
        File file = path.resolve(name).toFile();

        return  file.exists();

    }
}
