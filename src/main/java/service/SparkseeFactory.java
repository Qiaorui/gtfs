package service;


import com.sparsity.sparksee.gdb.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;

/**
 * Created by qiaorui on 23/12/16.
 */

public class SparkseeFactory {


    private static volatile SparkseeFactory instance;
    //private static final String LICENSE_KEY = "Y682C-1F03H-3JC4Y-23KV2";
    //private static final String DB_FILE = DataImporter.SPARKSEE_DIR + DataImporter.SPARKSEE_FILE;
    private Sparksee sparksee;
    private Session sess;
    private Database db;

    private static final int TIMEPOINT_DAY = 0;
    private static final int TIMEPOINT_NO_FUNICULAR = 1;
    private static final int TIMEPOINT_NIGHTBUS = 2;
    private static final int TIMEPOINT_NO_SUBWAY = 3;


    public static SparkseeFactory getInstance() {
        if (instance == null) {
            synchronized (SparkseeFactory.class) {
                if (instance == null) {
                    instance = new SparkseeFactory();
                }
            }
        }
        return instance;
    }

    private SparkseeFactory() {

    }

    private void initialize() {

        SparkseeConfig config = new SparkseeConfig();

        //config.setLicense(Environment.getLicenseKey());
        //config.setCacheMaxSize(100);
        //config.setRecoveryEnabled(true);
        config.setLogLevel(LogLevel.Debug);
        config.setLogFile( "temp.log");

        sparksee = new Sparksee(config);
        try {
            db = sparksee.open("tplanner.gdb", false);
            sess = db.newSession();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (sess == null) {
            return;
        }
        sess.close();
        db.close();
        sparksee.close();
        sess = null;
        db = null;
        sparksee = null;
    }

    private boolean isFileExists() {
        return new File("tplanner.gdb").exists();
    }

    public boolean isDBEmpty() {
        if (!isFileExists()) {
            return true;
        }
        if (sess == null) {
            initialize();
        }
        Graph g = sess.getGraph();
        return g.countNodes() == 0;
    }

    public Session getSession() {
        if (sess == null) {
            initialize();
        }
        return sess;
    }

    public String getItinerary(long date, int departTime, long fromPlace, long toPlace) {
        if (sess == null) {
            initialize();
        }
        if (fromPlace == toPlace) {
            return "";
        }

        String result = "";
        int timepoint = toTimepoint(date, departTime);
        Graph g = sess.getGraph();

        int itineraryType = g.findType("ITINERARY");
        int itineraryTimepointType = g.findAttribute(itineraryType, "TIMEPOINT");
        int itineraryJSONType = g.findAttribute(itineraryType, "JSON");

        Value value = new Value();

        Objects edges1 = g.edges(itineraryType, fromPlace, toPlace);
        //System.out.println("edge 1 : " + edges1.size());
        Objects edges2 = g.select(itineraryTimepointType, Condition.Equal, value.setInteger(timepoint));
        //System.out.println("edge 2 : " + edges2.size());
        edges1.intersection(edges2);

        if (edges1.size() != 1) {
            System.out.println(" edge size not 1 : " + edges1.size() + " timepoint: " + timepoint
                    + "  date: " + date + " time: " + departTime
            );
            System.exit(3);
        }
        TextStream tstrm = g.getAttributeText(edges1.any(), itineraryJSONType);
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
            result = str.toString();
        }
        tstrm.close();
        edges1.close();
        edges2.close();

        return result;
    }

    public long findPlaceId(int eventId) {
        if (sess == null) {
            initialize();
        }

        Graph g = sess.getGraph();

        Value value = new Value();

        int placeType = g.findType("PLACE");
        int eventType = g.findType("EVENT");
        int eventIdType = g.findAttribute(eventType, "ID");
        int runType = g.findType("RUN");


        long eventOid = g.findObject(eventIdType, value.setLong(eventId));
        Objects place = g.neighbors(eventOid, runType, EdgesDirection.Ingoing);
        long result = place.any();
        place.close();
        return result;
    }

    private int toTimepoint(long date, int time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);

        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                if (time > 24*60) {
                    return TIMEPOINT_NO_SUBWAY;
                }
                if (time > 22 * 60 + 30) {
                    return TIMEPOINT_NIGHTBUS;
                }
                if (time > 20 * 60) {
                    return TIMEPOINT_NO_FUNICULAR;
                }
                return TIMEPOINT_DAY;

            case Calendar.TUESDAY:
                if (time > 24*60) {
                    return TIMEPOINT_NO_SUBWAY;
                }
                if (time > 22 * 60 + 30) {
                    return TIMEPOINT_NIGHTBUS;
                }
                if (time > 20 * 60) {
                    return TIMEPOINT_NO_FUNICULAR;
                }
                return TIMEPOINT_DAY;

            case Calendar.WEDNESDAY:
                if (time > 24*60) {
                    return TIMEPOINT_NO_SUBWAY;
                }
                if (time > 22 * 60 + 30) {
                    return TIMEPOINT_NIGHTBUS;
                }
                if (time > 20 * 60) {
                    return TIMEPOINT_NO_FUNICULAR;
                }
                return TIMEPOINT_DAY;

            case Calendar.THURSDAY:
                if (time > 24*60) {
                    return TIMEPOINT_NO_SUBWAY;
                }
                if (time > 22 * 60 + 30) {
                    return TIMEPOINT_NIGHTBUS;
                }
                if (time > 20 * 60) {
                    return TIMEPOINT_NO_FUNICULAR;
                }
                return TIMEPOINT_DAY;

            case Calendar.FRIDAY:
                if (time > 26*60) {
                    return TIMEPOINT_NO_SUBWAY;
                }
                if (time > 22 * 60 + 30) {
                    return TIMEPOINT_NIGHTBUS;
                }
                if (time > 20 * 60) {
                    return TIMEPOINT_NO_FUNICULAR;
                }
                return TIMEPOINT_DAY;

            case Calendar.SATURDAY:
                if (time > 22 * 60 + 30) {
                    return TIMEPOINT_NIGHTBUS;
                }
                if (time > 20 * 60) {
                    return TIMEPOINT_NO_FUNICULAR;
                }
                return TIMEPOINT_DAY;

            case Calendar.SUNDAY:
                if (time > 24*60) {
                    return TIMEPOINT_NO_SUBWAY;
                }
                if (time > 22 * 60 + 30) {
                    return TIMEPOINT_NIGHTBUS;
                }
                if (time > 20 * 60) {
                    return TIMEPOINT_NO_FUNICULAR;
                }
                return TIMEPOINT_DAY;

            default:
                return -1;
        }
    }

}
