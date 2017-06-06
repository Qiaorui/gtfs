package xplanner.model;

/**
 * Created by qiaoruixiang on 05/06/2017.
 */
public class Schedule {

    private int day;
    private int openTime;
    private int closeTime;

    public Schedule(int day, int openTime, int closeTime) {
        this.day = day;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public int getDay() {
        return day;
    }

    public int getOpenTime() {
        return openTime;
    }

    public int getCloseTime() {
        return closeTime;
    }
}
