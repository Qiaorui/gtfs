package xplanner.model.gtfs;

import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.model.StopTime;
import org.onebusaway.gtfs.model.Transfer;

import java.util.*;

/**
 * Created by qiaoruixiang on 16/05/2017.
 */
public class GtfsLibrary {

    private static final int MIN_TRANSFER_TIME = 60; // 60 seconds


    public static Set<Stop> getActiveStops(Collection<StopTime> stopTimes) {
        Set<Stop> result = new HashSet<Stop>();
        for (StopTime stopTime : stopTimes) {
            result.add(stopTime.getStop());
        }
        return result;
    }

    //if No exists transfer between two stop, return -1 .
    public static int getTransferTime(Stop from, Stop to, Collection<Transfer> transfers) {
        int returnTime = -1;

        for (Transfer transfer : transfers) {
            if (transfer.getFromStop().equals(from) && transfer.getToStop().equals(to)) {
                return transfer.getMinTransferTime();
            }
            if (transfer.getToStop().equals(from) && transfer.getFromStop().equals(to)) {
                returnTime = transfer.getMinTransferTime();
            }
        }
        if (returnTime > 0) {
            return returnTime;
        } else {
            System.out.println("WARNING: No transfer is detected, using default min transfer time");
            return MIN_TRANSFER_TIME;
        }

    }

}
