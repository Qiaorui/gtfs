import Service.OTPService;
import com.sparsity.sparksee.gdb.*;
import com.sparsity.sparksee.gdb.Objects;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.onebusaway.gtfs.model.*;
import xplanner.model.RouteMode;
import xplanner.model.gtfs.GtfsData;
import xplanner.model.Place;
import xplanner.model.PlainEvent;
import xplanner.model.TimeWindows;
import xplanner.model.gtfs.GtfsLibrary;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by qiaoruixiang on 15/05/2017.
 */
public class SparkseeWriter {


    private Sparksee sparksee;
    private Database db;
    private Session sess;


    public SparkseeWriter() {

    }

    public void init() {
        SparkseeConfig cfg = new SparkseeConfig();
        sparksee = new Sparksee(cfg);
        //cfg.setCacheMaxSize(1000);
        //cfg.setRecoveryEnabled(true);
    }


    public void testData() {
        System.out.println("READING data");

        SparkseeConfig cfg = new SparkseeConfig();
        Sparksee sparksee = new Sparksee(cfg);
        try {
            Database db = sparksee.open("xplanner.gdb", true);

            Session sess = db.newSession();
            Graph g = sess.getGraph();

            Value value = new Value();

            /*
            TypeList typeList = g.findTypes();
            for (Integer type : typeList) {
                Objects objects = g.select(type);
                System.out.println(type + ": " + g.getType(type).getName() + " has " + objects.count() + " rows");
                objects.close();
            }
            */

            TypeList typeList = g.findTypes();
            for (Integer type : typeList) {
                System.out.println(type + " = " + g.getType(type).getName());
                AttributeList attributeList = g.findAttributes(type);

                Objects objects = g.select(type);
                System.out.println("\t size: " + objects.size());
                /*
                for (long obj : objects) {
                    System.out.println("    " + obj);
                    for (int attr : g.getAttributes(obj)) {
                        String output = "";
                        switch (g.getAttribute(attr).getDataType()) {
                            case Boolean:
                                g.getAttribute(obj, attr, value);
                                output = String.valueOf(value.getBoolean());
                                break;
                            case Double:
                                g.getAttribute(obj, attr, value);
                                output = String.valueOf(value.getDouble());
                                break;
                            case Integer:
                                g.getAttribute(obj, attr, value);
                                output = String.valueOf(value.getInteger());
                                break;
                            case Long:
                                g.getAttribute(obj, attr, value);
                                output = String.valueOf(value.getLong());
                                break;
                            case OID:
                                g.getAttribute(obj, attr, value);
                                output = String.valueOf(value.getOID());
                                break;
                            case String:
                                g.getAttribute(obj, attr, value);
                                output = value.getString();
                                break;
                            case Text:
                                break;

                                TextStream tstrm = g.getAttributeText(obj, attr);
                                if (!tstrm.isNull())
                                {
                                    int read;
                                    StringBuffer str = new StringBuffer();
                                    do
                                    {
                                        char[] buff = new char[10];
                                        read = tstrm.read(buff, 10);
                                        str.append(buff, 0, read);
                                    }
                                    while (read > 0);
                                    output = str.toString();
                                }
                                tstrm.close();
                                break;

                            case Timestamp:
                                g.getAttribute(obj, attr, value);
                                output = String.valueOf(value.getTimestampAsDate());
                                break;
                            default:
                                System.out.println("Error type " + g.getAttribute(attr).getDataType());
                                System.exit(13);

                        }
                        System.out.println("        " + g.getAttribute(attr).getName() + " : " + output);
                    }

                }
                */
                for (Integer attr : attributeList) {
                    System.out.println("----" + attr + " = " + g.getAttribute(attr).getName());
                }

                objects.close();
            }

            sess.close();
            db.close();
            sparksee.close();

        } catch (FileNotFoundException e) {
            System.out.println("Sparksee Database not found: " + e.getMessage());
        }

    }


    public void close() {
        sess.close();
        db.close();
        sparksee.close();
    }

    public void createSchema(String dbPath, String dbName) {
        System.out.println("Creating new Sparksee Database");

        try {
            db = sparksee.create(dbPath, dbName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        sess = db.newSession();

        Graph g = sess.getGraph();


        //
        // SCHEMA
        //

        int poiType = g.newNodeType("POI");
        g.newAttribute(poiType, "ID", DataType.Long, AttributeKind.Unique);
        g.newAttribute(poiType, "DURATION", DataType.Integer, AttributeKind.Basic);
        g.newAttribute(poiType, "SCORE", DataType.Integer, AttributeKind.Basic);
        g.newAttribute(poiType, "HIGHLIGHT", DataType.Boolean, AttributeKind.Basic);

        int venueType = g.newNodeType("VENUE");
        g.newAttribute(venueType, "LATITUDE", DataType.Double, AttributeKind.Indexed);
        g.newAttribute(venueType, "LONGITUDE", DataType.Double, AttributeKind.Indexed);

        int categoryType = g.newNodeType("CATEGORY");
        g.newAttribute(categoryType, "NAME", DataType.String, AttributeKind.Unique);

        int stopType = g.newNodeType("STOP");
        g.newAttribute(stopType, "ID", DataType.String, AttributeKind.Unique);
        g.newAttribute(stopType, "LATITUDE", DataType.Double, AttributeKind.Indexed);
        g.newAttribute(stopType, "LONGITUDE", DataType.Double, AttributeKind.Indexed);


        // Add a directed edge type restricted to go from people to movie for the director of a movie
        int scoringType = g.newRestrictedEdgeType("SCORING", poiType, categoryType, true);
        g.newAttribute(scoringType, "SCORE", DataType.Double, AttributeKind.Basic);

        int runType = g.newRestrictedEdgeType("RUN", venueType, poiType, true);
        g.newAttribute(runType, "DAY", DataType.Timestamp, AttributeKind.Indexed);
        g.newAttribute(runType, "OPEN_TIME", DataType.Integer, AttributeKind.Indexed);
        g.newAttribute(runType, "CLOSE_TIME", DataType.Integer, AttributeKind.Indexed);

        int routeType = g.newEdgeType("ROUTE", true, true);
        g.newAttribute(routeType, "DURATION", DataType.Integer, AttributeKind.Basic);
        g.newAttribute(routeType, "MODE", DataType.Integer, AttributeKind.Basic);


    }

    public void buildData(ArrayList<PlainEvent> events, Set<Place> places) {

        Graph g = sess.getGraph();

        int poiType = g.findType("POI");
        int poiIdType = g.findAttribute(poiType, "ID");
        int poiDurationType = g.findAttribute(poiType, "DURATION");
        int poiScoreType = g.findAttribute(poiType, "SCORE");
        int poiHighlightType = g.findAttribute(poiType, "HIGHLIGHT");

        int venueType = g.findType("VENUE");
        int venueLatitudeType = g.findAttribute(venueType, "LATITUDE");
        int venueLongitudeType = g.findAttribute(venueType, "LONGITUDE");

        int categoryType = g.findType("CATEGORY");
        int categoryNameType = g.findAttribute(categoryType, "NAME");

        //int stopType = g.findType("STOP");
        //int stopIdType = g.findAttribute(stopType, "ID");
        //int stopLatitudeType = g.findAttribute(stopType, "LATITUDE");
        //int stopLongitudeType = g.findAttribute(stopType, "LONGITUDE");


        int scoringType = g.findType("SCORING");
        int scoringScoreType = g.findAttribute(scoringType, "SCORE");

        int runType = g.findType("RUN");
        int runDayType = g.findAttribute(runType, "DAY");
        int runOpenType = g.findAttribute(runType, "OPEN_TIME");
        int runCloseType = g.findAttribute(runType, "CLOSE_TIME");

        //int routeType = g.findType("ROUTE");
        //int routeDurationType = g.findAttribute(routeType, "DURATION");
        //int routeModeType = g.findAttribute(routeType, "MODE");

        //
        // DATA
        //
        Value value = new Value();

        // add places
        for (Place p : places) {
            long place = g.newNode(venueType);
            g.setAttribute(place, venueLatitudeType, value.setDouble(Double.parseDouble(p.getLatitude())));
            g.setAttribute(place, venueLongitudeType, value.setDouble(Double.parseDouble(p.getLongitude())));
        }

        for (int i = 0; i < events.size(); i++) {
            long event = g.newNode(poiType);
            g.setAttribute(event, poiIdType, value.setLong(i));
            g.setAttribute(event, poiDurationType, value.setInteger(events.get(i).getDuration()));
            g.setAttribute(event, poiHighlightType, value.setBoolean(events.get(i).isHighlight()));
            g.setAttribute(event, poiScoreType, value.setInteger(events.get(i).getScore()));


            // event -> category
            for (Map.Entry<String, Double> entry : events.get(i).getCategory().entrySet()) {
                long cat = g.findOrCreateObject(categoryNameType, value.setString(entry.getKey()));
                long scoring = g.newEdge(scoringType, event, cat);
                g.setAttribute(scoring, scoringScoreType, value.setDouble(entry.getValue()));
            }

            // add time windows
            long place = findPlace(
                    Double.parseDouble(events.get(i).getLatitude()),
                    Double.parseDouble(events.get(i).getLongitude()),
                    g,
                    venueLatitudeType,
                    venueLongitudeType,
                    value
            );
            for (Map.Entry<Integer, ArrayList<TimeWindows>> day : events.get(i).getTimeWindows().entrySet()){
                for (TimeWindows tw : day.getValue()) {
                    long run = g.newEdge(runType, place, event);
                    //TODO: this is beta
                    g.setAttribute(run, runDayType, value.setTimestamp(2016,9,day.getKey(),0,0,0,0));
                    g.setAttribute(run, runOpenType, value.setInteger(tw.getOpenTime()));
                    g.setAttribute(run, runCloseType, value.setInteger(tw.getCloseTime()));
                }
            }

        }
    }



    public void writeGTFS(GtfsData gtfsData) {

        Graph g = sess.getGraph();
        Value value = new Value();

        int stopType = g.findType("STOP");
        int stopIdType = g.findAttribute(stopType, "ID");
        int stopLatitudeType = g.findAttribute(stopType, "LATITUDE");
        int stopLongitudeType = g.findAttribute(stopType, "LONGITUDE");

        int routeType = g.findType("ROUTE");
        int routeDurationType = g.findAttribute(routeType, "DURATION");
        int routeModeType = g.findAttribute(routeType, "MODE");

        // 1. find all active stops by filter stop times
        // 2. find parent if they have
        // 3. create TRANSFER route between brothers
        // 4. create TRANSIT route between stops that belong to the same route

        Set<Stop> activeStops = GtfsLibrary.getActiveStops(gtfsData.getStore().getAllStopTimes());
        Collection<Transfer> transfers = gtfsData.getStore().getAllTransfers();
        // find parent
        Map<String, List<Stop>> parent_child = new HashMap<>();

        for (Stop stop : activeStops) {

            long newStop = g.newNode(stopType);
            g.setAttribute(newStop, stopIdType, value.setString(stop.getId().getId()));
            g.setAttribute(newStop, stopLatitudeType, value.setDouble(stop.getLat()));
            g.setAttribute(newStop, stopLongitudeType, value.setDouble(stop.getLon()));


            if ( !(stop.getParentStation() == null || stop.getParentStation().isEmpty()) ) {
                if (parent_child.containsKey(stop.getParentStation())) {
                    //In this case, we have a brother so we create link
                    for (Stop brother :  parent_child.get(stop.getParentStation())) {
                        long brotherOid = g.findObject(stopIdType, value.setString(brother.getId().getId()));
                        long transfer1 = g.newEdge(routeType, newStop, brotherOid);
                        g.setAttribute(transfer1, routeDurationType,
                                value.setInteger(GtfsLibrary.getTransferTime(stop, brother, transfers))
                        );
                        g.setAttribute(transfer1, routeModeType, value.setInteger(RouteMode.TRANSFER.ordinal()));

                        long transfer2 = g.newEdge(routeType, brotherOid, newStop);
                        g.setAttribute(transfer2, routeDurationType,
                                value.setInteger(GtfsLibrary.getTransferTime(brother, stop, transfers))
                        );
                        g.setAttribute(transfer2, routeModeType, value.setInteger(RouteMode.TRANSFER.ordinal()));

                    }

                    parent_child.get(stop.getParentStation()).add(stop);
                } else {
                    List<Stop> stops = new ArrayList<>();
                    stops.add(stop);
                    parent_child.put(stop.getParentStation(), stops);
                }
            }
        }

        // Double check from transfer side
        for (Transfer transfer : transfers) {
            long s1 = g.findObject(stopIdType, value.setString(transfer.getFromStop().getId().getId()));
            long s2 = g.findObject(stopIdType, value.setString(transfer.getToStop().getId().getId()));

            boolean found = false;
            Objects objs = g.edges(routeType, s1, s2);
            for (long obj : objs) {
                g.getAttribute(obj, routeModeType, value);

                if (value.getInteger() == RouteMode.TRANSFER.ordinal()) {
                    found = true;
                    break;
                }
            }
            objs.close();

            if (!found) {
                long transfer1 = g.newEdge(routeType, s1, s2);
                g.setAttribute(transfer1, routeDurationType, value.setInteger(transfer.getMinTransferTime()));
                g.setAttribute(transfer1, routeModeType, value.setInteger(RouteMode.TRANSFER.ordinal()));

                long transfer2 = g.newEdge(routeType, s2, s1);
                g.setAttribute(transfer2, routeDurationType, value.setInteger(transfer.getMinTransferTime()));
                g.setAttribute(transfer2, routeModeType, value.setInteger(RouteMode.TRANSFER.ordinal()));

            }
        }


        // Find all trips that belong to the same route
        for (Route route : gtfsData.getStore().getAllRoutes()) {
            System.out.println("building route " + route.getShortName());

            // mapping all possible routes
            Map<Stop, List<Stop>> routeMap = new HashMap<>();
            for (Trip trip : gtfsData.getStore().getTripsForRoute(route)) {

                // reset from stop
                Stop from = null;
                Stop to;
                for (StopTime stopTime : gtfsData.getStore().getStopTimesForTrip(trip)) {
                    to = stopTime.getStop();


                    if (from == null) {
                        from = to;
                        continue;
                    }

                    if (!routeMap.containsKey(from)) {
                        routeMap.put(from, new ArrayList<Stop>());
                    }
                    if (!routeMap.get(from).contains(to)) {
                        routeMap.get(from).add(to);
                    }
                    from = to;
                }
            }

            // write mapped route into sparksee
            routeMap.forEach((k, v) -> {
                long from = g.findObject(stopIdType, value.setString(k.getId().getId()));
                v.forEach(stop -> {
                    long to = g.findObject(stopIdType, value.setString(stop.getId().getId()));
                    long edge = g.newEdge(routeType, from, to);
                    g.setAttribute(edge, routeModeType, value.setInteger(RouteMode.TRANSIT.ordinal()));
                });

            });

        }

    }



    //TODO: We will need this for generic data input
    public void precalculateRoutes() {
        Graph g = sess.getGraph();
        Value value = new Value();

        int stopType = g.findType("STOP");
        int stopIdType = g.findAttribute(stopType, "ID");
        int stopLatitudeType = g.findAttribute(stopType, "LATITUDE");
        int stopLongitudeType = g.findAttribute(stopType, "LONGITUDE");

        int routeType = g.findType("ROUTE");
        int routeDurationType = g.findAttribute(routeType, "DURATION");
        int routeModeType = g.findAttribute(routeType, "MODE");

        int venueType = g.findType("VENUE");
        int venueLatitudeType = g.findAttribute(venueType, "LATITUDE");
        int venueLongitudeType = g.findAttribute(venueType, "LONGITUDE");

        // stop <-> stop  walking route
        // stop <-> venue walking route
        // venue <-> venue unknown or walking route

        OTPService otp = new OTPService();

        Objects venues = g.select(venueType);

        // venue to venue
        int i = 0;
        int max = venues.size();
        ProgressIndicator.getInstance().setText("Sparksee: building GTFS data -- venue to venue : ");
        ProgressIndicator.getInstance().setMax(max);
        for (long from : venues) {
            ProgressIndicator.getInstance().setValue(i);

            g.getAttribute(from, venueLatitudeType, value);
            double fromLat = value.getDouble();
            g.getAttribute(from, venueLongitudeType, value);
            double fromLon = value.getDouble();



            for (long to : venues) {
                if (from == to) continue;
                g.getAttribute(to, venueLatitudeType, value);
                double toLat = value.getDouble();
                g.getAttribute(to, venueLongitudeType, value);
                double toLon = value.getDouble();
                JSONArray result = otp.getItineraryJSON(fromLat, fromLon, toLat, toLon, "7-12-2016", "14:30", "TRANSIT,WALK");

                long newRoute = g.newEdge(routeType, from, to);
                int duration = JsonUtils.getAvgDuration(result);
                g.setAttribute(newRoute, routeDurationType, value.setInteger(duration));
                g.setAttribute(newRoute, routeModeType, value.setInteger(RouteMode.HYBRID.ordinal()));
            }

            ++i;

        }


        // stop to stop
        Objects stops = g.select(stopType);
        max = stops.size();
        i = 0;

        ProgressIndicator.getInstance().setText("Sparksee: building GTFS data -- stop to stop : ");
        ProgressIndicator.getInstance().setMax(max);

        for (long stop : stops) {
            ProgressIndicator.getInstance().setValue(i);
            g.getAttribute(stop, stopLatitudeType, value);
            double fromLat = value.getDouble();
            g.getAttribute(stop, stopLongitudeType, value);
            double fromLon = value.getDouble();

            long[] neighbours = findClosestObjects(stopType, 800, fromLat, fromLon);
            precalculateNeighbours(stopType, stopType, stop, neighbours, false);

            ++i;
        }

        // venue to stop
        max = venues.size();
        i = 0;

        ProgressIndicator.getInstance().setText("Sparksee: building GTFS data -- venue to stop : ");
        ProgressIndicator.getInstance().setMax(max);


        for (long venue : venues) {
            ProgressIndicator.getInstance().setValue(i);
            g.getAttribute(venue, venueLatitudeType, value);
            double fromLat = value.getDouble();
            g.getAttribute(venue, venueLongitudeType, value);
            double fromLon = value.getDouble();

            long[] neighbours = findClosestObjects(stopType, 1000, fromLat, fromLon);
            precalculateNeighbours(venueType, stopType, venue, neighbours,  true);
            ++i;

        }


        stops.close();
        venues.close();



    }


    private long findPlace(double lat, double lon, Graph g, int latType, int lonType, Value value) {
        long result = -1;
        Objects lats = g.select(latType, Condition.Equal, value.setDouble(lat));
        Objects lons = g.select(lonType, Condition.Equal, value.setDouble(lon));
        Objects inter = Objects.combineIntersection(lats, lons);
        if (inter.count() != 1) {
            System.out.println("ERROR!!");
        } else {
            result = (long) inter.toArray()[0];
        }
        lats.close();
        lons.close();
        inter.close();
        return result;
    }


    private long[] findClosestObjects(int type, double d, double lat, double lon) {
        Graph g = sess.getGraph();
        Value value = new Value();
        Value value2 = new Value();

        int latitudeType = g.findAttribute(type, "LATITUDE");
        int longitudeType = g.findAttribute(type, "LONGITUDE");



        double r_earth = 6378000;
        double pi = Math.PI;

        double diffLat = d/r_earth*(180/pi);
        double diffLon = d/r_earth*(180/pi) / Math.cos(lat * pi / 180);

        Objects os1 = g.select(latitudeType, Condition.Between, value.setDouble(lat - diffLat), value2.setDouble(lat + diffLat));
        Objects os2 = g.select(longitudeType, Condition.Between, value.setDouble(lon - diffLon), value2.setDouble(lon + diffLon));
        Objects intersects = Objects.combineIntersection(os1, os2);
        long[] result = new long[intersects.size()];
        int i = 0;
        for (long obj : intersects) {
            result[i] = obj;
            ++i;
        }

        intersects.close();
        os1.close();
        os2.close();

        return result;
    }

    // Precalculate walking time between points
    private void precalculateNeighbours(int fromType, int toType, long from, long[] tos, boolean isCommutative) {
        Graph g = sess.getGraph();
        Value value = new Value();

        int fromLatitudeType = g.findAttribute(fromType, "LATITUDE");
        int fromLongitudeType = g.findAttribute(fromType, "LONGITUDE");

        int routeType = g.findType("ROUTE");
        int routeDurationType = g.findAttribute(routeType, "DURATION");
        int routeModeType = g.findAttribute(routeType, "MODE");

        int toLatitudeType = g.findAttribute(toType, "LATITUDE");
        int toLongitudeType = g.findAttribute(toType, "LONGITUDE");

        OTPService otp = new OTPService();

        double fromLat, fromLon;
        g.getAttribute(from, fromLatitudeType, value);
        fromLat = value.getDouble();
        g.getAttribute(from, fromLongitudeType, value);
        fromLon = value.getDouble();

        for (long to : tos) {
            if (from == to) continue;

            double toLat, toLon;
            g.getAttribute(to, toLatitudeType, value);
            toLat = value.getDouble();
            g.getAttribute(to, toLongitudeType, value);
            toLon = value.getDouble();

            JSONArray result = otp.getItineraryJSON(fromLat, fromLon, toLat, toLon, "7-12-2016", "14:30", "TRANSIT,WALK");
            // if best route is walking, then we can link them
            if (JsonUtils.isWalking((JSONObject) result.get(0))) {
                int duration = JsonUtils.getDuration((JSONObject)result.get(0));

                long newRoute = g.newEdge(routeType, from, to);
                g.setAttribute(newRoute, routeModeType, value.setInteger(RouteMode.WALK.ordinal()));
                g.setAttribute(newRoute, routeDurationType, value.setInteger(duration));

                if (isCommutative) {
                    long newRoute2 = g.newEdge(routeType, to, from);
                    g.setAttribute(newRoute2, routeModeType, value.setInteger(RouteMode.WALK.ordinal()));
                    g.setAttribute(newRoute2, routeDurationType, value.setInteger(duration));
                }
            }


        }


    }


}
