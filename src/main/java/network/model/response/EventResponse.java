package network.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public class EventResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("duration")
    private Integer duration;

    @SerializedName("isHighlight")
    private Boolean isHighlight;

    @SerializedName("image")
    private String image;

    @SerializedName("schedules")
    private List<ScheduleResponse> schedules;

    @SerializedName("location")
    private PlaceResponse place;

    @SerializedName("categories")
    private List<CategoryScoreResponse> categoryScores;
    //@SerializedName("categories")
    //private List<Long> categoriesIDs;


    public EventResponse(Long id, String name, String description, Integer duration, Boolean isHighlight, String image, List<ScheduleResponse> schedules, PlaceResponse place, List<CategoryScoreResponse> categoryScores) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.isHighlight = isHighlight;
        this.image = image;
        this.schedules = schedules;
        this.place = place;
        this.categoryScores = categoryScores;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Boolean getHighlight() {
        return isHighlight;
    }

    public void setHighlight(Boolean highlight) {
        isHighlight = highlight;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<ScheduleResponse> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<ScheduleResponse> schedules) {
        this.schedules = schedules;
    }

    public PlaceResponse getPlace() {
        return place;
    }

    public void setPlace(PlaceResponse place) {
        this.place = place;
    }

    public List<CategoryScoreResponse> getCategoryScores() {
        return categoryScores;
    }

    public void setCategoryScores(List<CategoryScoreResponse> categoryScores) {
        this.categoryScores = categoryScores;
    }
}
