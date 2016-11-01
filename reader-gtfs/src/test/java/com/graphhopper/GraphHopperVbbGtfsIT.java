package com.graphhopper;

import com.graphhopper.reader.gtfs.GraphHopperGtfs;
import com.graphhopper.util.Helper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static com.graphhopper.reader.gtfs.GtfsHelper.time;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class GraphHopperVbbGtfsIT {

    private static final String GRAPH_LOC = "target/graphhopperIT-vbb-gtfs";
    private static GraphHopperGtfs graphHopper;

    @BeforeClass
    public static void init() {
        Helper.removeDir(new File(GRAPH_LOC));

        graphHopper = new GraphHopperGtfs();
        graphHopper.setGtfsFile("files/vbb.zip");
        graphHopper.setGraphHopperLocation(GRAPH_LOC);
        graphHopper.importOrLoad();
    }

    @AfterClass
    public static void tearDown() {
        if (graphHopper != null)
            graphHopper.close();
    }

    @Test
    public void testMichazCommute() {
        final double FROM_LAT = 52.483669, FROM_LON = 13.423147;
        final double TO_LAT = 52.51959, TO_LON = 13.321266;
        assertRouteWeightIs(graphHopper, FROM_LAT, FROM_LON, time(9, 0), TO_LAT, TO_LON, time(9, 40));
    }

    private void assertRouteWeightIs(GraphHopperGtfs graphHopper, double from_lat, double from_lon, int earliestDepartureTime, double to_lat, double to_lon, int expectedWeight) {
        GHRequest ghRequest = new GHRequest(
                from_lat, from_lon,
                to_lat, to_lon
        );
        ghRequest.getHints().put(GraphHopperGtfs.EARLIEST_DEPARTURE_TIME_HINT, earliestDepartureTime);
        GHResponse route = graphHopper.route(ghRequest);
        System.out.println(route);
        System.out.println(route.getBest());
        System.out.println(route.getBest().getDebugInfo());

        assertFalse(route.hasErrors());
        assertEquals("Expected weight == scheduled arrival time", expectedWeight, route.getBest().getRouteWeight(), 0.1);
    }
}
