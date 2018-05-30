package de.thm.genomeData.sql;

import de.thm.misc.ChromosomSizes;
import de.thm.misc.Genome;
import de.thm.run.BackendServer;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DBConnectorTest {
    @Test
    public void getAllCellLines() throws Exception {

        BackendServer.dbfilepath = "/home/menzel/Desktop/THM/lfba/enhort/stefan.db";
        BackendServer.basePath = new File("/home/menzel/Desktop/THM/lfba/enhort/dat/stefan/").toPath();

        DBConnector connector = new DBConnector();
        connector.connect();
        SortedMap<String, List<String>> allCellLines = connector.getAllCellLines();

        assertNull(allCellLines.get("ACHN"));
        assertNull(allCellLines.get("HeLa-S3"));
        assertNull(allCellLines.get("GM12878"));
    }

    @Test
    public void getChrSizes() throws Exception {
        DBConnector connector = new DBConnector();
        connector.connect();

        assertEquals((long) ChromosomSizes.getInstance().getChrSize(Genome.Assembly.hg19, "chr5"), 180915260);

        assertEquals((long) ChromosomSizes.getInstance().getChrSize(Genome.Assembly.GRCh38, "chr12"), 133275309);

    }

}