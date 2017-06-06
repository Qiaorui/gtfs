package xplanner.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import service.RestClient;

import java.util.*;

/**
 * Created by qiaoruixiang on 15/05/2017.
 */
public class PoiData {

    private ArrayList<PlainEvent> events;
    private Set<Place> places;
    private List<Poi> pois;

    public PoiData() {
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

    public void readDataFromWeb(String url) {
        RestClient restClient = new RestClient(url);
        String jsonStr = restClient.executeGet();
        pois = parsePoisJson(jsonStr);
        pois.forEach(item -> places.add(new Place(item.getLatitude(), item.getLongitude())));
    }

    public ArrayList<PlainEvent> getEvents() {
        return events;
    }

    public Set<Place> getPlaces() {
        return places;
    }

    public List<Poi> getPois() {
        return pois;
    }

    public static List<Poi> parsePoisJson(String jsonStr) {
        ArrayList<Poi> result = new ArrayList<>();
        try {
            JSONObject root = new JSONObject(jsonStr);
            JSONArray pois = root.getJSONArray("POIs");

            for (int i = 0; i < pois.length(); i++) {
                JSONObject poiJson = pois.getJSONObject(i);
                Poi poi = new Poi(poiJson);
                result.add(poi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


}
