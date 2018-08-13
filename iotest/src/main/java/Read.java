// Copyright (C) 2018 Michael Menzel
// 
// This file is part of Enhort. <https://enhort.mni.thm.de>.
// 
// Enhort is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
// 
// Enhort is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with Enhort.  If not, see <https://www.gnu.org/licenses/>.  
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
