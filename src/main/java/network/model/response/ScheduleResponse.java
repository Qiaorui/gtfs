package network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public class ScheduleResponse {
    @SerializedName("openTime")
    private Long startTime;

    @SerializedName("closeTime")
    private Long endTime;

    public ScheduleResponse(Long startTime, Long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
