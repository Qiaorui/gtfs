package service;

import com.sparsity.sparksee.algorithms.SinglePairShortestPathDijkstra;
import com.sparsity.sparksee.gdb.*;

/**
 * Created by qiaoruixiang on 14/06/2017.
 */
public class RouteGenerator {


    //private Context mContext;

    public RouteGenerator() {

    }

    /*
    public RouteGenerator(Context context) {
        mContext = context;
    }
    */


    public String generateRoute(double fromLat, double fromLon, double toLat, double toLon, int day, int time) {
        String result = "";
        Session session = SparkseeFactory.getInstance().getSession();
        Graph g = session.getGraph();

        Value value = new Value();

        int poiType = g.findType("POI");
        int poiIdType = g.findAttribute(poiType, "ID");
        int poiDurationType = g.findAttribute(poiType, "DURATION");
        int poiScoreType = g.findAttribute(poiType, "BASE_SCORE");
        int poiHighlightType = g.findAttribute(poiType, "HIGHLIGHT");

        int venueType = g.findType("VENUE");
        int venueLatitudeType = g.findAttribute(venueType, "LATITUDE");
        int venueLongitudeType = g.findAttribute(venueType, "LONGITUDE");

        int categoryType = g.findType("CATEGORY");
        int categoryNameType = g.findAttribute(categoryType, "NAME");

        int stopType = g.findType("STOP");
        int stopIdType = g.findAttribute(stopType, "ID");
        int stopLatitudeType = g.findAttribute(stopType, "LATITUDE");
        int stopLongitudeType = g.findAttribute(stopType, "LONGITUDE");


        int scoringType = g.findType("SCORING");
        int scoringScoreType = g.findAttribute(scoringType, "SCORE");

        int runType = g.findType("RUN");
        int runDayType = g.findAttribute(runType, "DAY");
        int runOpenType = g.findAttribute(runType, "OPEN_TIME");
        int runCloseType = g.findAttribute(runType, "CLOSE_TIME");

        int routeType = g.findType("ROUTE");
        int routeDurationType = g.findAttribute(routeType, "DURATION");
        int routeModeType = g.findAttribute(routeType, "MODE");
        int routeIdType = g.findAttribute(routeType, "ROUTE_ID");

        int auxiliaryType = g.findType("AUXILIARY");
        int auxiliaryNameType = g.findAttribute(auxiliaryType, "NAME");
        int auxiliaryLatitudeType = g.findAttribute(auxiliaryType, "LATITUDE");
        int auxiliaryLongitudeType = g.findAttribute(auxiliaryType, "LONGITUDE");

        long src = g.findObject(auxiliaryNameType, value.setString("start"));
        long dst = g.findObject(auxiliaryNameType, value.setString("end"));

        g.setAttribute(src, auxiliaryLatitudeType, value.setDouble(fromLat));
        g.setAttribute(src, auxiliaryLongitudeType, value.setDouble(fromLon));
        g.setAttribute(dst, auxiliaryLatitudeType, value.setDouble(toLat));
        g.setAttribute(dst, auxiliaryLongitudeType, value.setDouble(toLon));

        CustomDijkstraCost dijkstraCostCalculator = new CustomDijkstraCost(day, time);

        SinglePairShortestPathDijkstra spDijkstra = new SinglePairShortestPathDijkstra(session, src, dst);
        spDijkstra.setDynamicEdgeCostCallback(dijkstraCostCalculator);
        spDijkstra.addEdgeType(routeType, EdgesDirection.Outgoing);
        spDijkstra.addNodeType(stopType);
        spDijkstra.addNodeType(auxiliaryType);
        spDijkstra.run();
        if (spDijkstra.exists())
        {
            System.out.println("Cost: " + spDijkstra.getCost());
            OIDList pathAsNodes;
            pathAsNodes = spDijkstra.getPathAsNodes();
            System.out.println("Route size " + pathAsNodes.count());
            OIDListIterator pathIt;
            pathIt = pathAsNodes.iterator();
            while (pathIt.hasNext())
            {
                long nodeid;
                nodeid = pathIt.next();
                System.out.println(" -> " + nodeid);
                //System.out.println(nodeid);
            }
        }
        spDijkstra.close();
        System.out.println(" Route Finish");


        return result;
    }





}
