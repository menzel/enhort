package de.thm.genomeData;

import java.io.*;
import java.nio.file.Path;

/**
 * Created by Michael Menzel on 15/12/15.
 */
public class IntervalDumper {
    private Path baseDir;

    public IntervalDumper(Path baseDir) {
        this.baseDir = baseDir;
    }

    public void dumpInterval(Interval interval, String name){

        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(baseDir.resolve(name).toString()));

            objectOutputStream.writeObject(interval);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Interval getInterval(File file){
        Interval interval;
        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
            interval = (Interval) input.readObject();

            return interval;

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
