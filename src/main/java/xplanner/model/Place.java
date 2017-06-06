package xplanner.model;

import java.util.Objects;

/**
 * Created by qiaorui on 21/11/16.
 */
public class Place {

    public enum VertexType {
        NORMAL, BIKESHARE, BIKEPARK, TRANSIT
    }

    private String locationName;
    private Double latitude;
    private Double longitude;
    private String address;
    private String district;

    private String vertexType;

    public Place(String name, double latitude, double longitude) {
        locationName = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Place(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Place(String locationName, double latitude, double longitude, String address, String district) {
        this.locationName = locationName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.district = district;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getVertexType() {
        return vertexType;
    }

    public void setVertexType(String vertexType) {
        this.vertexType = vertexType;
    }

    @Override
    public String toString(){
        String result = locationName + "(" + vertexType + ": " + latitude + "," +longitude + ")";
        if (vertexType.equals("EVENT")) {
            result += "  Address: " + address + "  District: " + district;
        }

        return result;
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
        return Objects.equals(latitude, place.latitude) && Objects.equals(longitude, place.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude);
    }

}
