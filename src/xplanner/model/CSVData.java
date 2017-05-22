package xplanner.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaoruixiang on 15/05/2017.
 */
public class CSVData {

    private ArrayList<PlainEvent> events;
    private Set<Place> places;

    public CSVData() {
        events = new ArrayList<>();
        places = new HashSet<>();
    }

    public void readDataFromFile(String f) {
        CSVReader csvReader = new CSVReader(f);
        while (csvReader.hasNext()) {
            PlainEvent event = new PlainEvent();
            event.bindData(csvReader.read());
            events.add(event);
        }
        events.forEach( item -> places.add(item.getPlace()));
    }

    public ArrayList<PlainEvent> getEvents() {
        return events;
    }

    public Set<Place> getPlaces() {
        return places;
    }
}
