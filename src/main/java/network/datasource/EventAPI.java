package network.datasource;

import network.model.response.EventResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public interface EventAPI {

    void getAllEventByLastDate(GetEventsCallback callback, Long festivalID, long timestamp, String lang);

    List<EventResponse> getAllEventByLastDate(Long festivalID, long timestamp, String lang) throws IOException;


    interface GetEventsCallback {
        void onEventsRetrieved(List<EventResponse> eventsResponse);
        void onEventsRetrievedError(int errorCode);
    }


}
