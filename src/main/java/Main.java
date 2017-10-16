import gtfs.GtfsData;
import network.datasource.CategoryAPI;
import network.datasource.EventAPI;
import network.datasource.impl.CategoryAPIImpl;
import network.datasource.impl.EventAPIImpl;
import network.model.response.CategoryResponse;
import network.model.response.EventResponse;
import org.onebusaway.csv_entities.schema.DefaultEntitySchemaFactory;
import org.onebusaway.gtfs.serialization.GtfsEntitySchemaFactory;
import org.onebusaway.gtfs.serialization.GtfsWriter;
import utils.Const;
import xplanner.model.PoiData;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {


    public static void main(String[] args) {

        GtfsData gtfs = new GtfsData();

        try {
            /*
            Process p = Runtime.getRuntime().exec("python " +
                    "/Users/qiaoruixiang/development/workspace/python/transitfeed/feedvalidator.py " +
            //        "-p -d " +
                    args[0]);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;

            System.out.println("Feed Validator:");
            while ((s = stdInput.readLine()) != null) {
                System.out.println("\t" + s);
            }


            System.out.println("Error msg:");
            while ((s = stdError.readLine()) != null) {
                System.out.println("\t" + s);
            }
            */

            ProgressIndicator.getInstance().init();
            ProgressIndicator.getInstance().setText("creating GTFS objects");
            gtfs.readGtfsFile(new File(args[0]));



            ProgressIndicator.getInstance().setText("rebuilding GTFS");
            //Assume the gtfs is clean and correct
            gtfs.rebuild();

            //gtfs.fillTransfer();

            ProgressIndicator.getInstance().setText("reading POIs");
            EventAPI eventAPI = new EventAPIImpl();
            List<EventResponse> events = eventAPI.getAllEventByLastDate(Const.FESTIVAL_TOKEN, 0, null);
            System.out.println("Retrieved pois: " + events.size());
            PoiData poiData = new PoiData();
            poiData.readFromData(events);
            //poiData.readDataFromWeb("http://xplanner-cigo.herokuapp.com/api/pois");

            ProgressIndicator.getInstance().setText("initializing sparksee");
            SparkseeWriter sparkseeWriter = new SparkseeWriter();
            sparkseeWriter.init();
            ProgressIndicator.getInstance().setText("creating sparksee schema");
            sparkseeWriter.createSchema(Const.SPARKSEE_DB_FILE, Const.SPARKSEE_DB);
            ProgressIndicator.getInstance().setText("Sparksee: building POIs data");
            sparkseeWriter.buildData(poiData.getPois(), poiData.getPlaces());


            ProgressIndicator.getInstance().setText("Sparksee: building GTFS data");
            sparkseeWriter.writeGTFS(gtfs);

            ProgressIndicator.getInstance().setText("Sparksee: building auxiliary point");
            sparkseeWriter.setUpAuxiliaryPoints();

            ProgressIndicator.getInstance().setText("Sparksee: precalculating routes");
            sparkseeWriter.precalculateRoutes();
            sparkseeWriter.close();
            sparkseeWriter.testData();
            //for (StopTime stopTime : store.getAllStopTimes()) {
            //    System.out.println(stopTime);
            //}
        /*
        for (Transfer transfer : store.getAllTransfers()) {
            System.out.println("from " + transfer.getFromStop().getId() + " to " + transfer.getToStop().getId() +
            " using " + transfer.getMinTransferTime());
        }
        */
        /*
        // Access entities through the store
        Map<AgencyAndId, Route> routesById = store.getEntitiesByIdForEntityType(
                AgencyAndId.class, Route.class);
        System.out.println("agencyID:" + Arrays.asList(routesById.keySet()));

        for (Route route : routesById.values()) {
            System.out.println("route: " + route.getShortName());
        }

        gtfs.removeUnusedStops();

        File outputDir = new File("output");
        if (!outputDir.exists()) outputDir.mkdirs();

        GtfsWriter writer = new GtfsWriter();
        writer.setOutputLocation(outputDir);

        DefaultEntitySchemaFactory schemaFactory = new DefaultEntitySchemaFactory();
        schemaFactory.addFactory(GtfsEntitySchemaFactory.createEntitySchemaFactory());

        writer.setEntitySchemaFactory(schemaFactory);
        try {
            writer.run(gtfs.getStore());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

            ProgressIndicator.getInstance().close();



        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(0);
    }





}
