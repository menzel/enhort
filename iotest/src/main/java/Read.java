import edu.unc.genomics.io.BedFileReader;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Read {


    public static void main(String[] args) {

        final Logger log = Logger.getLogger(Read.class);

        Read read = new Read();
        read.read();


    }



    void read(){
         try {

            Path path = new File("/home/menzel/Desktop/THM/lfba/enhort/skripte/exons.bed").toPath();
            BedFileReader bfr = new BedFileReader(path);

            bfr.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
