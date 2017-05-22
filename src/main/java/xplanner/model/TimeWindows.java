package xplanner.model;

/**
 * Created by qiaorui on 16/11/16.
 */
public class TimeWindows {
    private int openTime;
    private int closeTime;

    public TimeWindows(int openTime, int closeTime) {
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public TimeWindows(String o, String c, int duration) {
        openTime = convertTimer(o);
        closeTime = convertTimer(c);
        if (openTime > closeTime) {
            closeTime += 24 * 60;
        } else if (openTime < 4 * 60) {
            openTime += 24 * 60;
            closeTime += 24 * 60;
        }
        closeTime = Math.max(openTime, closeTime - duration);
    }

    public int getOpenTime() {
        return openTime;
    }

    public void setOpenTime(int openTime) {
        this.openTime = openTime;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(int closeTime) {
        this.closeTime = closeTime;
    }

    private int convertTimer(String s) {
        String[] splited = s.split(":");
        return Integer.parseInt(splited[0]) * 60 + Integer.parseInt(splited[1]);
    }

    @Override
    public String toString() {
        return "(" + openTime + "," + closeTime + ")";
    }
}
