package de.thm.run;

import de.thm.bootstrap.Analyse;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;

import java.io.File;

public class Main {

    public static void main(String[] args) {


        Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projekphase/HIV-hg19.bed"));
        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));
        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projekphase/MLV.bed"));


        Analyse analyse = new Analyse();
        analyse.analyse(userDat);

    }
}
