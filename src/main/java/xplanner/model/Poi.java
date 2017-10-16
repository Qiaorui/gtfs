package xplanner.model;

import network.model.response.CategoryScoreResponse;
import network.model.response.EventResponse;
import network.model.response.ScheduleResponse;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaoruixiang on 02/06/2017.
 */
public class Poi {

    private long id;
    private String name;
    private int duration;
    private int baseScore;
    private String address;
    private String location;
    private boolean isHighlight;
    private String description;
    private String image;
    private String poiType;
    private double latitude;
    private double longitude;

    // store raw and parsed info
    private String rawSchedules;
    private String rawCategories;
    private Map<String, Integer> categories;
    private Map<Long, Integer> categoryScores;

    private List<Schedule> schedules;

    public Poi(JSONObject json) {
        bindData(json);
    }

    public Poi() {

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public String getAddress() {
        return address;
    }

    public String getLocation() {
        return location;
    }

    public boolean isHighlight() {
        return isHighlight;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getPoiType() {
        return poiType;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getRawSchedules() {
        return rawSchedules;
    }

    public String getRawCategories() {
        return rawCategories;
    }

    public Map<String, Integer> getCategories() {
        return categories;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public Map<Long, Integer> getCategoryScores() {
        return categoryScores;
    }

    public void bindData(JSONObject json) {
        try {
            id = json.getLong("id");
            name = json.getString("name");
            duration = json.getInt("duration");
            baseScore = json.getInt("baseScore");
            location = json.getString("location");
            address = json.getString("address");
            isHighlight = json.getBoolean("isHighlight");
            description = json.getString("description");
            latitude = json.getDouble("latitude");
            longitude = json.getDouble("longitude");
            image = json.getString("image");
            poiType = json.getString("POIType");
            JSONArray schedulesJson = json.getJSONArray("schedules");
            JSONArray categoriesJson = json.getJSONArray("categories");
            rawSchedules = schedulesJson.toString();
            rawCategories = categoriesJson.toString();

            categories = new HashMap<>();
            schedules = new ArrayList<>();

            for (int i = 0; i < categoriesJson.length(); i++) {
                JSONObject cat = categoriesJson.getJSONObject(i);
                categories.put(cat.getString("name"), cat.getInt("score"));
            }

            for (int i = 0; i < schedulesJson.length(); i++) {
                JSONObject sch = schedulesJson.getJSONObject(i);
                String[] date = sch.getString("day").substring(0,10).split("-");
                int day = Integer.parseInt(date[0]) * 10000 + Integer.parseInt(date[1]) * 100 + Integer.parseInt(date[2]);
                Schedule schedule = new Schedule(day, stringToTime(sch.getString("openTime")), stringToTime(sch.getString("closeTime")));
                schedules.add(schedule);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void bindData(EventResponse response) {
        id = response.getId();
        name = response.getName();
        description = response.getDescription();
        duration = response.getDuration();
        address = response.getPlace().getAddress();
        location = response.getPlace().getName();
        isHighlight = response.getHighlight();
        image = response.getImage();
        poiType = "event";
        latitude = response.getPlace().getLatitude();
        longitude = response.getPlace().getLongitude();

        categoryScores = new HashMap<>();
        for (CategoryScoreResponse c : response.getCategoryScores()) {
            categoryScores.put(c.getCategoryID(), c.getScore());
        }
        schedules = new ArrayList<>();
        for (ScheduleResponse s : response.getSchedules()) {
            DateTime start = new DateTime(s.getStartTime() * 1000);
            DateTime end = new DateTime(s.getEndTime() * 1000);
            int day = start.getYear() * 10000 + start.getMonthOfYear() * 100 + start.getDayOfMonth();
            int startTime = start.getHourOfDay() * 60 + start.getMinuteOfHour();
            int endTime = end.getHourOfDay() * 60 + end.getMinuteOfHour();
            endTime = (endTime < startTime) ? endTime + 24*60 : endTime;
            schedules.add(new Schedule(day, startTime, endTime));
        }
    }


    private int stringToTime(String s) {
        String[] splited = s.split(":");
        return Integer.parseInt(splited[0]) * 60 + Integer.parseInt(splited[1]);
    }

    @Override
    public String toString() {

        return id + ": " + name + " ||| " + rawCategories + " ||| " + rawSchedules;
    }
}
