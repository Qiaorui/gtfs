package network.service;

import network.model.response.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public interface CategoryService {

    @GET("festivals/{festivalID}/categories")
    Call<List<CategoryResponse>> getCategories(@Path("festivalID") long festivalID, @Query("lang") String lang);

}
