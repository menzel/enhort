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
 * Dumps intervals to binary files for faster loading
 *
 * Created by Michael Menzel on 15/12/15.
 */
final class IntervalDumper {
    private static final String extension = "kryo";
    private final Path baseDir;
    private final Kryo kryo;

    IntervalDumper(Path baseDir) {
        this.baseDir = baseDir;
        kryo = new Kryo();
    }

    /**
     *
     * @param interval - dumps an interval to a directory
     * @param filename - name of original file without extension
     */
    void dumpInterval(Interval interval, String filename){

        filename = filename.substring(0,filename.indexOf(".")) + ".kryo";

        try (Output output = new Output(new FileOutputStream(baseDir.resolve("kryo/" + filename).toString()))) {

            kryo.writeObject(output, interval);
            output.flush();
            output.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    /**
     * Creates an interval from a given binary file
     *
     * @param file - binary file to parse
     * @return Interval based on given binary
     */
    Interval getInterval(File file){
        Interval interval;
        String name = file.getName();
        name = name.substring(0,name.indexOf(".")) + ".kryo";

        try {
            Input input = new Input(new FileInputStream(baseDir + "/kryo/" + name));
            interval = kryo.readObject(input, GenomeInterval.class); //TODO use different objects

            input.close();
            return interval;

        } catch (FileNotFoundException e) {
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Checks if a binary file exists to a given bed file
     *
     * @param name - bed file (with extension) *
     * @return true if exists. False otherwise
     */
    boolean exists(String name) {
        name = name.substring(0,name.indexOf(".")) + ".kryo";
        Path path = baseDir.resolve("kryo");
        File file = path.resolve(name).toFile();

        return  file.exists();

    }
}
