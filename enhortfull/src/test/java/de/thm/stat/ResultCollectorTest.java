package de.thm.stat;

import de.thm.logo.GenomeFactory;
import de.thm.positionData.Sites;
import de.thm.result.ResultCollector;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * TODO
 * Created by menzel on 6/27/17.
 */
public class ResultCollectorTest {

    private static ResultCollector r;

    @BeforeClass
    public static void setUp() throws Exception {
        Sites sites = mock(Sites.class);
        when(sites.getPositionCount()).thenReturn(42);
        r = new ResultCollector(sites, GenomeFactory.Assembly.hg19, new ArrayList<>());


        //create results

        for(int i = 0; i < 5; i++){
            TestResult result = mock(TestResult.class);
            when(result.getpValue()).thenReturn(i/10.);
            when(result.getType()).thenReturn(TestResult.Type.inout);

            when(result.getMeasuredIn()).thenReturn(10);
            when(result.getExpectedIn()).thenReturn(5);

            when(result.getMeasuredOut()).thenReturn(50);
            when(result.getExpectedOut()).thenReturn(100);

            when(result.getName()).thenReturn("noname");


            r.addResult(result);
        }

        for(int i = 0; i < 6; i++){
            TestResult result = mock(TestResult.class);
            when(result.getpValue()).thenReturn(i/10.);
            when(result.getType()).thenReturn(TestResult.Type.score);
            when(result.getName()).thenReturn("noname");

            r.addResult(result);
        }


        for(int i = 0; i < 4; i++){
            TestResult result = mock(TestResult.class);
            when(result.getpValue()).thenReturn(i/12.);
            when(result.getType()).thenReturn(TestResult.Type.name);
            when(result.getName()).thenReturn("noname");

            r.addResult(result);
        }

        //create covariants

        //create hotspots
    }

    @org.junit.Test
    public void getScoredResults() throws Exception {
        assertEquals(6,r.getScoredResults(true).size());
        assertEquals(1,r.getScoredResults(false).size());
    }

    @Test
    public void getInOutResults() throws Exception {
        assertEquals(5,r.getInOutResults(true).size());
        assertEquals(1,r.getInOutResults(false).size());
    }

    @Test
    public void getNamedResults() throws Exception {

        assertEquals(4,r.getNamedResults(true).size());
        assertEquals(1,r.getNamedResults(false).size());
    }

    @Test
    public void getInsignificantResults() throws Exception {
        assertEquals(12, r.getInsignificantResults().size());
    }

    @Test
    public void getSignificantTrackCount() throws Exception {
        assertEquals(3,r.getSignificantTrackCount());
    }

    @Test
    public void getTrackCount() throws Exception {
        assertEquals(15,r.getTrackCount());
    }

    @Test
    public void getCovariants() throws Exception {
    }

    @Test
    public void getBarplotdata() throws Exception {
    }

    @Test
    public void getBarplotdataExport() throws Exception {
        String vals = "noname,noname,noname,noname,noname<br>pvalue,0.0,0.1,0.2,0.3,0.4<br>effectsize,0.0,0.0,0.0,0.0,0.0<br>MeasuredIn,10,10,10,10,10<br>ExpectedIn,5,5,5,5,5<br>MeasuredPercentIn,0.0,0.0,0.0,0.0,0.0<br>ExpectedPercentIn,0.0,0.0,0.0,0.0,0.0<br>";
        assertEquals(vals, r.getBarplotdataExport());
    }


    @Test
    public void getBgCount() throws Exception {
        assertEquals(42, r.getBgCount());
    }

}