package de.thm.genomeData.sql;

import de.thm.logo.GenomeFactory;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DBConnectorTest {
    @Test
    public void getAllCellLines() throws Exception {

        DBConnector connector = new DBConnector();
        connector.connect();
        SortedMap<String, List<String>> allCellLines = connector.getAllCellLines();

        assertNull(allCellLines.get("ACHN"));
        assertNull(allCellLines.get("HeLa-S3"));
        assertEquals(36, allCellLines.get("GM").size());
    }

    @Test
    public void getAllTracks() throws Exception {
        DBConnector connector = new DBConnector();
        connector.connect();

        assertEquals(481, connector.getAllTracks().size());
        assertEquals(481, connector.getAllTracks().size());
    }

    @Test
    public void getChrSizes() throws Exception {
        DBConnector connector = new DBConnector();
        connector.connect();

        Map<GenomeFactory.Assembly, Map<String, Integer>> chrSizes = connector.getChrSizes();

        assertEquals(chrSizes.get(GenomeFactory.Assembly.hg19).get("chr5"), (Integer)180915260);
        assertEquals(chrSizes.get(GenomeFactory.Assembly.hg38).get("chr12"), (Integer)133275309);

    }

}