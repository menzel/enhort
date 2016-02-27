package de.thm.run;

import de.thm.bootstrap.Analyse;
import de.thm.genomeData.TrackFactory;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;

import java.io.File;

public class Main {

    public static void main(String[] args) {


        Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projekphase/HIV-hg19.bed").toPath());
        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));
        //Sites userDat = new UserData(new File("/home/menzel/Downloads/sites_andreas/copy.tab").toPath());

        TrackFactory.getInstance().loadIntervals();

        Analyse analyse = new Analyse();
        try {
            analyse.analyse(userDat);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
