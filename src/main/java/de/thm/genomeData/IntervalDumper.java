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
public final class IntervalDumper {
    private Path baseDir;
    private Kryo kryo;
    private static final String extension = "kryo";

    public IntervalDumper(Path baseDir) {
        this.baseDir = baseDir;
        kryo = new Kryo();
    }

    /**
     * returns the extension of binary files
     * @return - extension name without dot
     */
    public static String getExtension() {
        return extension;
    }

    /**
     *
     * @param interval - dumps an interval to a directory
     * @param filename - name of original file without extension
     */
    public void dumpInterval(Interval interval, String filename){

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

    /**
     * Checks if a binary file exists to a given bed file
     *
     * @param name - bed file (with extension) *
     * @return true if exists. False otherwise
     */
    public boolean exists(String name) {
        name = name.substring(0,name.indexOf(".")) + ".kryo";
        Path path = baseDir.resolve("kryo");
        File file = path.resolve(name).toFile();

        return  file.exists();

    }
}
