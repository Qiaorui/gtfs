package xplanner.model;

import java.util.Map;

/**
 * Created by qiaoruixiang on 15/05/2017.
 */
public class PlainEvent extends Event {
    public String location;
    public String address;
    public String district;
    public String latitude;
    public String longitude;

    private String originalTW;
    private String originalCat;


    PlainEvent() {
        super();
    }

    @Override
    public void bindData(Map<String, String> data) {
        super.bindData(data);
        location = data.get("location");
        address = data.get("address");
        district = data.get("district");
        latitude = data.get("latitude");
        longitude = data.get("longitude");
        originalCat = data.get("category");
        originalTW = data.get("time_windows");
    }

    @Override
    public String toString() {
        return super.toString() + " , " + originalTW + " , " + originalCat;
    }
}
