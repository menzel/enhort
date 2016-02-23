package de.thm.backgroundModel;

import de.thm.genomeData.GenomeInterval;
import de.thm.genomeData.Interval;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michael Menzel on 13/1/16.
 */
public class AppearanceTableTest {

    @Test
    public void testHash() throws Exception {
        AppearanceTable table = new AppearanceTable();
        List<Interval> list = new ArrayList<>();
        list.add(new GenomeInterval());
        list.add(new GenomeInterval());
        list.add(new GenomeInterval());
        list.add(new GenomeInterval());

        System.out.println(table.hash(list));
        System.out.println(table.hash(list.subList(0,1)));
        System.out.println(table.hash(list.subList(1,2)));
        System.out.println(table.hash(list.subList(2,3)));
        System.out.println(table.hash(list.subList(3,4)));
        System.out.println(table.hash(list.subList(2,4)));
        System.out.println(table.hash(list.subList(1,4)));
        System.out.println(table.hash(list.subList(4,4)));

    }
}