package network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public class DayResponse {

    @SerializedName("day")
    private String day;

    public DayResponse(String day) {
        this.day = day;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
