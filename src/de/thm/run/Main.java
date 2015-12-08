package de.thm.run;

import de.thm.positionData.SimpleBackgroundModel;
import de.thm.positionData.Sites;
import de.thm.positionData.UserData;

import java.io.File;

public class Main {

    public static void main(String[] args) {

        Sites userDat = new UserData();
        ((UserData) userDat).loadPositionsFromFile(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));

        Sites bg = new SimpleBackgroundModel(userDat.getPositionCount());

    }
}
