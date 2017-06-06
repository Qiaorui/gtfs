
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 * Created by qiaoruixiang on 19/05/2017.
 */
public class JsonUtils {
    public static int getAvgDuration(JSONArray jsonArray) {
        int acc = 0;
        int times = 0;
        for (int i = 0; i < jsonArray.size() && i < 2; i++) {
            int duration;

            Object obj = ((JSONObject)jsonArray.get(i)).get("duration");
            if (obj instanceof Integer) {
                duration = (int)obj;
            } else {
                duration = Math.toIntExact((long)obj);
            }

            acc +=  duration;
            ++times;
        }
        return acc/times;
    }

    public static boolean isWalking(JSONObject json) {
        int duration, walkTime;

        Object obj = json.get("duration");
        if (obj instanceof Integer) {
            duration = (int)obj;
        } else {
            duration = Math.toIntExact((long)obj);
        }
        obj = json.get("walkTime");
        if (obj instanceof Integer) {
            walkTime = (int)obj;
        } else {
            walkTime = Math.toIntExact((long)obj);
        }

        //System.out.println(duration + " = " + walkTime + "   : " + (duration == walkTime));
        return duration == walkTime;
    }

    public static int getDuration(JSONObject json) {
        Object obj = json.get("duration");
        if (obj instanceof Integer) {
            return (int)obj;
        } else {
            return Math.toIntExact((long)obj);
        }
    }


}
