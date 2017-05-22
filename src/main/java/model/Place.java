package model;

import java.util.Objects;

/**
 * Created by qiaoruixiang on 14/05/2017.
 */
public class Place {
    private double lat;
    private double lng;

    public Place(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }


    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (getClass() != object.getClass()) {
            return false;
        }
        final Place place = (Place) object;
        return Objects.equals(lat, place.lat) && Objects.equals(lng, place.lng);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng);
    }

}
