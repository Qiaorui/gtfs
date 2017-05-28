package gtfs;

import org.onebusaway.gtfs.impl.GtfsRelationalDaoImpl;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.services.GtfsMutableRelationalDao;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by qiaoruixiang on 08/05/2017.
 */
public class GtfsData {

    private static int SECONDS_IN_HOUR = 60*60;
    private GtfsMutableRelationalDao store;

    public GtfsData(){

    }

    public void readGtfsFile(File f) throws IOException {

        GtfsReader reader = new GtfsReader();
        reader.setInputLocation(f);

        store = new GtfsRelationalDaoImpl();
        reader.setEntityStore(store);
        //GtfsDaoImpl store = new GtfsDaoImpl();
        //reader.setEntityStore(store);
        reader.run();
    }

    /*
    public void fillTransfer() {
        Collection<Transfer> transfers = store.getAllTransfers();
        List<Stop> stops = store.getAllStops()
                .stream()
                .filter(k->
                        !(k.getId().getId().startsWith("P") || k.getId().getId().startsWith("E")))
                .collect(Collectors.toList());


        HashMap<Place, List<Stop>> parent_child_stops = new HashMap<>();
        stops.forEach( k -> {
            Place p = new Place(k.getLat(),k.getLon());
            if (parent_child_stops.containsKey(p)) {
                parent_child_stops.get(p).add(k);
            } else {
                ArrayList<Stop> list = new ArrayList<>();
                list.add(k);
                parent_child_stops.put(p, list);
            }
        });
        Map<Place, List<Stop>> filtered = parent_child_stops.entrySet()
                .stream()
                .filter(k -> k.getValue().size() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        for (Map.Entry<Place, List<Stop>> item : filtered.entrySet()) {

            List<Stop> stopList = item.getValue();
            for (int i = 0; i < stopList.size(); i++) {
                boolean in_transfer = false;
                for (int j = i+1; j < stopList.size() && !in_transfer; j++) {
                    for (Transfer transfer : transfers) {
                        Stop fromStop = transfer.getFromStop();
                        Stop toStop = transfer.getToStop();

                        if ((fromStop.equals(stopList.get(i)) || toStop.equals(stopList.get(i))) &&
                                (fromStop.equals(stopList.get(j)) || toStop.equals(stopList.get(j)))) {
                            in_transfer = true;
                        }
                    }
                }
                if (!in_transfer) {
                    System.out.println("not in transfer " + stopList.get(i));
                }
            }


        }
        System.out.println("transfer: " + transfers.size());
        System.out.println("stops: " + stops.size());
        System.out.println("place: " + parent_child_stops.size());
        System.out.println("filtered: " + filtered.size());
    }
    */

    public void rebuild() {
        int beforeAgency = store.getAllAgencies().size();
        int beforeRoute = store.getAllRoutes().size();
        int beforeTrip = store.getAllTrips().size();
        int beforeCalendar = store.getAllCalendars().size();
        int beforeCalendarDate = store.getAllCalendarDates().size();
        int beforeStopTime = store.getAllStopTimes().size();
        int beforeStop = store.getAllStops().size();
        int beforeFareAttribute = store.getAllFareAttributes().size();
        int beforeFareRule = store.getAllFareRules().size();
        int beforeShape = store.getAllShapeIds().size();
        int beforeFrequency = store.getAllFrequencies().size();
        int beforeTransfer = store.getAllTransfers().size();
        int beforeFeedInfo = store.getAllFeedInfos().size();

        //correct stop time
        rebuildStopTime();


        System.out.println("*******************************************");
        System.out.println("\t\tRebuild Report\n");
        System.out.println("Agency\t\t" + beforeAgency + "\t->\t" + store.getAllAgencies().size());
        System.out.println("Route\t\t" + beforeRoute + "\t->\t" + store.getAllRoutes().size());
        System.out.println("Trip\t\t" + beforeTrip + "\t->\t" + store.getAllTrips().size());
        System.out.println("Calendar\t\t" + beforeCalendar + "\t->\t" + store.getAllCalendars().size());
        System.out.println("CalendarDate\t\t" + beforeCalendarDate + "\t->\t" + store.getAllCalendarDates().size());
        System.out.println("StopTime\t\t" + beforeStopTime + "\t->\t" + store.getAllStopTimes().size());
        System.out.println("Stop\t\t" + beforeStop + "\t->\t" + store.getAllStops().size());
        System.out.println("FareAttribute\t\t" + beforeFareAttribute + "\t->\t" + store.getAllFareAttributes().size());
        System.out.println("FareRule\t\t" + beforeFareRule + "\t->\t" + store.getAllFareRules().size());
        System.out.println("Shape\t\t" + beforeShape + "\t->\t" + store.getAllShapeIds().size());
        System.out.println("Frequency\t\t" + beforeFrequency + "\t->\t" + store.getAllFrequencies().size());
        System.out.println("Transfer\t\t" + beforeTransfer + "\t->\t" + store.getAllTransfers().size());
        System.out.println("FeedInfo\t\t" + beforeFeedInfo + "\t->\t" + store.getAllFeedInfos().size());

    }


    public void removeUnusedStops() {
        int beforeStop = store.getAllStops().size();

        store.getAllStops().retainAll(GtfsLibrary.getActiveStops(store.getAllStopTimes()));

        System.out.println("*******************************************");
        System.out.println("\t\tRemove Report\n");
        System.out.println("Stop\t\t" + beforeStop + "\t->\t" + store.getAllStops().size());

    }

    private void rebuildStopTime() {
        //Collection<ServiceCalendar> calendars = store.getAllCalendars();
        //Collection<ServiceCalendarDate> calendarDates = store.getAllCalendarDates();
        //Collection<Stop> stops = store.getAllStops();
        Collection<Trip> trips = store.getAllTrips();
        ArrayList<Trip> removeTrips = new ArrayList<>();
        for (Trip trip : trips) {
            List<StopTime> stopTimes = store.getStopTimesForTrip(trip);

            //System.out.println(trip);
            if (!store.getAllServiceIds().contains(trip.getServiceId())) {
                System.out.println("\tWARNING: The trip has no service, take out of the list");
                removeTrips.add(trip);
                continue;
            }
            /*
            ServiceCalendar sc = store.getCalendarForServiceId(trip.getServiceId());
            if (sc != null) {
                System.out.println(sc);
            } else {
                List<ServiceCalendarDate> dates = store.getCalendarDatesForServiceId(trip.getServiceId());
                System.out.println("\t" + Arrays.asList(dates));
            }
            */


            ArrayList<Integer> removedStopSequences = removeRepeatedStops(stopTimes);
            if (!removedStopSequences.isEmpty()) {
                System.out.println("stop time removed:");
                System.out.print("\t");
                System.out.println(Arrays.asList(removedStopSequences));
            }
            filterStopTimes(stopTimes);
            interpolateStopTimes(stopTimes);

            if (stopTimes.size() < 2 && store.getFrequenciesForTrip(trip).isEmpty()) {
                //need to check if it has some frequency

                System.out.println("\tWARNING: The trip has not enough stop time either frequency");
                removeTrips.add(trip);

                continue;
            }

            //print after result
            //for (StopTime st : stopTimes) {
                //System.out.println("\t" + st);
            //}

        }

        trips.removeAll(removeTrips);


    }


    public GtfsMutableRelationalDao getStore(){
        return store;
    }

    //Remove stop times repeated, which are identical.
    private ArrayList<Integer> removeRepeatedStops(List<StopTime> stopTimes) {
        StopTime prev = null;
        Iterator<StopTime> it = stopTimes.iterator();
        ArrayList<Integer> stopSequencesRemoved = new ArrayList<>();
        while (it.hasNext()) {
            StopTime st = it.next();
            if (prev != null) {
                if (prev.getStop().equals(st.getStop())) {
                    // OBA gives us unmodifiable lists, but we have copied them.

                    // Merge the two stop times, making sure we're not throwing out a stop time with times in favor of an
                    // interpolated stop time
                    // keep the arrival time of the previous stop, unless it didn't have an arrival time, in which case
                    // replace it with the arrival time of this stop time
                    // This is particularly important at the last stop in a route (see issue #2220)
                    if (prev.getArrivalTime() == StopTime.MISSING_VALUE) prev.setArrivalTime(st.getArrivalTime());

                    // prefer to replace with the departure time of this stop time, unless this stop time has no departure time
                    if (st.getDepartureTime() != StopTime.MISSING_VALUE) prev.setDepartureTime(st.getDepartureTime());

                    it.remove();
                    stopSequencesRemoved.add(st.getStopSequence());
                }
            }
            prev = st;
        }
        return stopSequencesRemoved;
    }


    //fix time missing and midnight cross problem
    private void filterStopTimes(List<StopTime> stopTimes) {

        if (stopTimes.size() < 2) return;
        StopTime st0 = stopTimes.get(0);

        /* Set departure time if it is missing */
        if (!st0.isDepartureTimeSet() && st0.isArrivalTimeSet()) {
            System.out.println("\tfill arrival time");
            st0.setDepartureTime(st0.getArrivalTime());
        }

        /* If the feed does not specify any timepoints, we want to mark all times that are present as timepoints. */
        boolean hasTimepoints = false;
        for (StopTime stopTime : stopTimes) {
            if (stopTime.getTimepoint() == 1) {
                hasTimepoints = true;
                break;
            }
        }
        // TODO verify that the first (and last?) stop should always be considered a timepoint.
        if (!hasTimepoints) st0.setTimepoint(1);

        /* Indicates that stop times in this trip are being shifted forward one day. */
        boolean midnightCrossed = false;

        for (int i = 1; i < stopTimes.size(); i++) {
            boolean st1bogus = false;
            StopTime st1 = stopTimes.get(i);

            /* If the feed did not specify any timepoints, mark all times that are present as timepoints. */
            if ( !hasTimepoints && (st1.isDepartureTimeSet() || st1.isArrivalTimeSet())) {
                st1.setTimepoint(1);
            }

            if (midnightCrossed) {
                if (st1.isDepartureTimeSet())
                    st1.setDepartureTime(st1.getDepartureTime() + 24 * SECONDS_IN_HOUR);
                if (st1.isArrivalTimeSet())
                    st1.setArrivalTime(st1.getArrivalTime() + 24 * SECONDS_IN_HOUR);
            }
            /* Set departure time if it is missing. */
            // TODO: doc: what if arrival time is missing?
            if (!st1.isDepartureTimeSet() && st1.isArrivalTimeSet()) {
                System.out.println("\tfill departure time");
                st1.setDepartureTime(st1.getArrivalTime());
            }
            /* Do not process (skip over) non-timepoint stoptimes, leaving them in place for interpolation. */
            // All non-timepoint stoptimes in a series will have identical arrival and departure values of MISSING_VALUE.
            if ( ! (st1.isArrivalTimeSet() && st1.isDepartureTimeSet())) {
                continue;
            }
            int dwellTime = st0.getDepartureTime() - st0.getArrivalTime();
            if (dwellTime < 0) {
                if (st0.getArrivalTime() > 23 * SECONDS_IN_HOUR && st0.getDepartureTime() < 1 * SECONDS_IN_HOUR) {
                    midnightCrossed = true;
                    st0.setDepartureTime(st0.getDepartureTime() + 24 * SECONDS_IN_HOUR);
                } else {
                    st0.setDepartureTime(st0.getArrivalTime());
                }
            }
            int runningTime = st1.getArrivalTime() - st0.getDepartureTime();

            if (runningTime < 0) {
                // negative hops are usually caused by incorrect coding of midnight crossings
                midnightCrossed = true;
                if (st0.getDepartureTime() > 23 * SECONDS_IN_HOUR && st1.getArrivalTime() < 1 * SECONDS_IN_HOUR) {
                    st1.setArrivalTime(st1.getArrivalTime() + 24 * SECONDS_IN_HOUR);
                } else {
                    st1.setArrivalTime(st0.getDepartureTime());
                }
            }

            if (st0.getArrivalTime() == st1.getArrivalTime() ||
                    st0.getDepartureTime() == st1.getDepartureTime()) {

                st1bogus = true;
            }
            // st0 should reflect the last stoptime that was not clearly incorrect
            if ( ! st1bogus) {
                st0 = st1;
            }

        } // END for loop over stop times
    }

    private void interpolateStopTimes(List<StopTime> stopTimes) {
        int lastStop = stopTimes.size() - 1;
        int numInterpStops = -1;
        int departureTime = -1, prevDepartureTime = -1;
        int interpStep = 0;

        int i;
        for (i = 0; i < lastStop; i++) {
            StopTime st0 = stopTimes.get(i);

            prevDepartureTime = departureTime;
            departureTime = st0.getDepartureTime();

            /* Interpolate, if necessary, the times of non-timepoint stops */
            /* genuine interpolation needed */
            if (!(st0.isDepartureTimeSet() && st0.isArrivalTimeSet())) {
                // figure out how many such stops there are in a row.
                int j;
                StopTime st = null;
                for (j = i + 1; j < lastStop + 1; ++j) {
                    st = stopTimes.get(j);
                    if ((st.isDepartureTimeSet() && st.getDepartureTime() != departureTime)
                            || (st.isArrivalTimeSet() && st.getArrivalTime() != departureTime)) {
                        break;
                    }
                }
                if (j == lastStop + 1) {
                    throw new RuntimeException(
                            "Could not interpolate arrival/departure time on stop " + i
                                    + " (missing final stop time) on trip " + st0.getTrip());
                }
                numInterpStops = j - i;
                int arrivalTime;
                if (st.isArrivalTimeSet()) {
                    arrivalTime = st.getArrivalTime();
                } else {
                    arrivalTime = st.getDepartureTime();
                }
                interpStep = (arrivalTime - prevDepartureTime) / (numInterpStops + 1);
                if (interpStep < 0) {
                    throw new RuntimeException(
                            "trip goes backwards for some reason");
                }
                for (j = i; j < i + numInterpStops; ++j) {
                    //System.out.println("interpolating " + j + " between " + prevDepartureTime + " and " + arrivalTime);
                    departureTime = prevDepartureTime + interpStep * (j - i + 1);
                    st = stopTimes.get(j);
                    if (st.isArrivalTimeSet()) {
                        departureTime = st.getArrivalTime();
                    } else {
                        st.setArrivalTime(departureTime);
                    }
                    if (!st.isDepartureTimeSet()) {
                        st.setDepartureTime(departureTime);
                    }
                }
                i = j - 1;
            }
        }
    }


}
