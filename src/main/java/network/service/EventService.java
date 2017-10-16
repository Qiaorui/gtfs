package network.service;

import network.model.response.EventResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public interface EventService {

    @GET("festivals/{festivalID}/pois")
    Call<List<EventResponse>> getEvents(@Path("festivalID") long festivalID, @Query("last_update") long timestamp, @Query("lang") String lang);

    @GET("festivals/{festivalID}/pois/{poiID}")
    Call<List<EventResponse>> getEvent(@Path("festivalID") long festivalID, @Path("poiID") long poiID, @Query("lang") String lang);


}
