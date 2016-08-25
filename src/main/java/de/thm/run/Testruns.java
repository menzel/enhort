package de.thm.run;

@Deprecated
public class Testruns {

    public static void main(String[] args) {


        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projektphase/MLV.bed").toPath());
        //Sites userDat = new UserData(new File("/home/menzel/Desktop/THM/lfba/projekphase/genomic_sites.sleeping_beauty.hg19.txt"));
        //Sites userDat = new UserData(new File("/home/menzel/Downloads/sites_andreas/copy.tab").toPath());

        /*
        Sites userDat = BackgroundModelFactory.createBackgroundModel(7489);

        System.out.println(userDat.getPositionCount());


        Intersect intersect = new Intersect();
        TrackFactory.getInstance().loadIntervals();

        InOutTrack track = null;
        for(Track t: TrackFactory.getInstance().getAllIntervals()){

            if(t.getName().equals("Exons")){
                track = (InOutTrack) t;
            }
        }

        System.out.println((Arrays.toString(intersect.getAverageDistance(track, userDat).toArray())));
        */

        Analyse analyse = new Analyse();
        try {
            analyse.timing_intersect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
