package network.service;

import network.model.response.FestivalResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public interface FestivalService {

    @GET("festivals/{id}")
    Call<FestivalResponse> getFestival(@Path("id") long festivalID, @Query("lang") String lang);

}
