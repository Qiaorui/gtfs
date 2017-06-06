package xplanner.model;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qiaorui on 16/11/16.
 */
public class Event {
    private String title;
    /*
    private String location;
    private String address;
    private String district;
    private double latitude;
    private double longitude;
    */
    private Place place;

    private String information;
    private String tag;
    private String schedule;
    private String image;
    private int score;
    private Map<String, Double> category;
    private int duration;
    private boolean highlight;
    private Map<Integer, ArrayList<TimeWindows>> timeWindows;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public Event() {
        category = new HashMap<>();
        timeWindows = new HashMap<>();
    }

    public double getLatitude() {
        return place.getLatitude();
    }

    public double getLongitude() {
        return place.getLongitude();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<String, Double> getCategory() {
        return category;
    }

    public void setCategory(Map<String, Double> category) {
        this.category = category;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isHighlight() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public Map<Integer, ArrayList<TimeWindows>> getTimeWindows() {
        return timeWindows;
    }

    public void setTimeWindows(Map<Integer, ArrayList<TimeWindows>> timeWindows) {
        this.timeWindows = timeWindows;
    }


    public void bindData(Map<String, String> data) {
        title = data.get("title");
        //place = new Place(data.get("location"), data.get("latitude"), data.get("longitude"), data.get("address"), data.get("district"));
        place.setVertexType("EVENT");
        information = data.get("information");
        highlight = data.get("highlight").equals("True");
        tag = data.get("tag");
        schedule = data.get("schedule");
        image = data.get("image");
        score = Integer.parseInt(data.get("score"));
        duration = Integer.parseInt(data.get("duration"));
        for (String cat : data.get("category").split(",")) {
            Pattern pattern = Pattern.compile("'(\\w+)': (\\d.\\d+)");
            Matcher matcher = pattern.matcher(cat);

            if (matcher.find()) {
                category.put(matcher.group(1), Double.parseDouble(matcher.group(2)));
            } else {
                System.out.println("Error");
                System.exit(2);
            }
        }

        for (String day : data.get("time_windows").split("],")) {
            int d = 0;
            ArrayList<TimeWindows> tws = new ArrayList<>();

            Pattern pattern = Pattern.compile("(\\d+): \\[.*");
            Matcher matcher = pattern.matcher(day);
            if (matcher.find()) {
                d = Integer.parseInt(matcher.group(1));
            }

            pattern = Pattern.compile("\\('(\\d+:\\d+)', '(\\d+:\\d+)'\\)");
            matcher = pattern.matcher((day));
            while (matcher.find()) {
                if (!tws.add(new TimeWindows(matcher.group(1), matcher.group(2), duration))) {
                    System.out.println("Error when add tw");
                    System.exit(3);
                }
            }
            timeWindows.put(d, tws);
        }

    }

    private String categoryToString() {
        String result = "";
        Set entrySet = category.entrySet();
        Iterator iter = entrySet.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            result += "[" + entry.getKey() + "] = " + entry.getValue() + " , ";
        }
        return result;
    }

    private String timeWindowsToString() {
        String result = "";
        Set entrySet = timeWindows.entrySet();
        Iterator iter = entrySet.iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            result += "(day: " + entry.getKey();

            for (TimeWindows tw : (ArrayList<TimeWindows>) entry.getValue()) {
                result += " [" + tw.getOpenTime() + ", " + tw.getCloseTime() + "]";
            }
            result += ")";

        }

        return result;
    }

    @Override
    public String toString() {

        String output = "title:" + title
                //+ ", location:" + location + ", address:" + address + ", district:" + district
                + place.toString()
                //+ ", information:" + information
                //+ ", tag:" + tag + ", schedule:" + schedule + ", image:" + image
                + ", score:" + score + ", duration:" + duration;

        //output = timeWindowsToString();


        if (highlight) {
            output = ANSI_BLUE + output + ANSI_RESET;
        }

        return output;
    }

    public int calculateScore(Map<String, Double> preference) {
        int result = 0;
        for (Map.Entry<String, Double> entry : category.entrySet()) {
            double factor = (highlight) ? 1.2 : 1.0;
            result += factor * entry.getValue() * preference.get(entry.getKey()) * score;
        }
        return result;
    }

}
