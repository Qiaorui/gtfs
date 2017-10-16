package service;

import com.sparsity.sparksee.algorithms.SinglePairShortestPathDijkstraDynamicCost;

/**
 * Created by qiaoruixiang on 14/06/2017.
 */
public class CustomDijkstraCost extends SinglePairShortestPathDijkstraDynamicCost {



    //private final Context mContext;
    private final int startTime;
    private final int day;
    /*
    private static final String[] TRIP_COLUMNS = {
            XContract.TripEntry.COLUMN_TRIP_ID,
            XContract.TripEntry.COLUMN_SERVICE_ID,
            XContract.TripEntry.COLUMN_WHEELCHAIR_ACCESSIBLE
    };

    private static final int COL_TRIP_ID = 0;
    private static final int COL_SERVICE_ID = 1;
    private static final int COL_WHEEL = 2;
*/



    public CustomDijkstraCost(int d, int t) {
        day = d;
        startTime = t;
    }

    @Override
    public double calculateEdgeCost(long sourceNode, double sourceCost, int sourceLevel, long targetNode, long edge, int edgeWeightAttr) {

        /*
        Session session = SparkseeFactory.getInstance().getSession();
        Graph g = session.getGraph();

        Value value = new Value();

        double result = -1;

        int routeType = g.findType("ROUTE");
        int routeDurationType = g.findAttribute(routeType, "DURATION");
        int routeModeType = g.findAttribute(routeType, "MODE");
        int routeIdType = g.findAttribute(routeType, "ROUTE_ID");

        // find route mode [ UNKNWON, WALK, HYBRID, TRANSIT, TRANSFER ]
        g.getAttribute(edge, routeModeType, value);
        RouteMode routeMode = RouteMode.values()[value.getInteger()];

        switch (routeMode) {
            case TRANSFER:
                g.getAttribute(edge, routeDurationType, value);
                result = value.getInteger();
                break;
            case WALK:
                g.getAttribute(edge, routeDurationType, value);
                result = value.getInteger();
                break;
            case HYBRID:
                g.getAttribute(edge, routeDurationType, value);
                result = value.getInteger();
                break;
            case TRANSIT: // Find by query sqlite database

                int stopType = g.findType("STOP");
                int stopIdType = g.findAttribute(stopType, "ID");

                g.getAttribute(sourceNode, stopIdType, value);
                String fromStop = value.getString();
                g.getAttribute(targetNode, stopIdType, value);
                String toStop = value.getString();
                g.getAttribute(edge, routeIdType, value);
                String routeId = value.getString();

                result = calculateTransitCost(fromStop, toStop, routeId, day, startTime + (int)sourceCost);

                break;
            case UNKNOWN: // unkown start point or end point, so we use graphhopper
                int fromType = g.getObjectType(sourceNode);
                int fromLatType = g.findAttribute(fromType, "LATITUDE");
                int fromLonType = g.findAttribute(fromType, "LONGITUDE");

                int toType = g.getObjectType(targetNode);
                int toLatType = g.findAttribute(toType, "LATITUDE");
                int toLonType = g.findAttribute(toType, "LONGITUDE");

                double fromLat, fromLon, toLat, toLon;

                g.getAttribute(sourceNode, fromLatType, value);
                fromLat = value.getDouble();
                g.getAttribute(sourceNode, fromLonType, value);
                fromLon = value.getDouble();
                g.getAttribute(targetNode, toLatType, value);
                toLat = value.getDouble();
                g.getAttribute(targetNode, toLonType, value);
                toLon = value.getDouble();

                double distance = Utility.distVincenty(fromLat, fromLon, toLat, toLon);
                if (distance > 1000) {
                    result = 1;
                } else {
                    // cost unit -> ms
                    long cost = GraphHopperManager.getInstance().getTripTime(fromLat, fromLon, toLat, toLon);
                    result = cost / 1000;
                }
                break;
            default:
                Log.e("Routing", " No such route type " + routeMode);
                result = -1;
        }


        return result;
    }

    private int calculateTransitCost(String fromStop, String toStop, String routeId, int day, int departureTime) {

        int cost = -1;

        // use route id to find all possible trip
        // use departure time find correct service id
        // cross check to get correct trip
        // find stop time elapsed in stop_times using fromStop and toStop

        // TODO: Optimize SQL query
        Cursor cursor  = mContext.getContentResolver().query(
                XContract.TripEntry.CONTENT_URI,
                TRIP_COLUMNS,
                XContract.TripEntry.COLUMN_ROUTE_ID + " = ?",
                new String[] {routeId},
                null
        );
        ArrayList<Trip> trips = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                trips.add(new Trip(cursor.getString(COL_TRIP_ID), routeId, cursor.getString(COL_SERVICE_ID), cursor.getInt(COL_WHEEL) != 0));
            }
        } finally {
            cursor.close();
        }



        return cost;
        */
        return 10;

    }


}
