package network.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public class FestivalResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("name")
    private String name;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    @SerializedName("slogan")
    private String slogan;

    @SerializedName("description")
    private String description;

    @SerializedName("poster")
    private String poster;

    @SerializedName("supportedLanguages")
    private List<LanguageResponse> supportedLanguages;

    @SerializedName("mainLanguage")
    private LanguageResponse mainLanguage;

    @SerializedName("timeZoneID")
    private String timeZoneID;

    @SerializedName("days")
    private List<DayResponse> days;

    @SerializedName("version")
    private Integer version;

    public FestivalResponse(Long id, String name, String city, String country, String slogan, String description, String poster, List<LanguageResponse> supportedLanguages, LanguageResponse mainLanguage, String timeZoneID, List<DayResponse> days, Integer version) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.slogan = slogan;
        this.description = description;
        this.poster = poster;
        this.supportedLanguages = supportedLanguages;
        this.mainLanguage = mainLanguage;
        this.timeZoneID = timeZoneID;
        this.days = days;
        this.version = version;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public List<LanguageResponse> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(List<LanguageResponse> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public LanguageResponse getMainLanguage() {
        return mainLanguage;
    }

    public void setMainLanguage(LanguageResponse mainLanguage) {
        this.mainLanguage = mainLanguage;
    }

    public String getTimeZoneID() {
        return timeZoneID;
    }

    public void setTimeZoneID(String timeZoneID) {
        this.timeZoneID = timeZoneID;
    }

    public List<DayResponse> getDays() {
        return days;
    }

    public void setDays(List<DayResponse> days) {
        this.days = days;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
