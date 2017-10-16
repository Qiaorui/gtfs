package network.utils;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import network.service.CategoryService;
import network.service.EventService;
import network.service.FestivalService;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    /**
     * Get Retrofit Instance
     */
    private static Retrofit.Builder getRetrofitInstance(Interceptor interceptor) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
//                        .readTimeout(ServiceUtils.READ_TIME_OUT, TimeUnit.SECONDS)
//                        .connectTimeout(ServiceUtils.CONNECT_TIME_OUT, TimeUnit.SECONDS)
//                        .writeTimeout(ServiceUtils.WRITE_TIME_OUT, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .addInterceptor(interceptor).build())
                .addConverterFactory(GsonConverterFactory.create(gson));

    }

    /**
     * Get Event Service API.
     *
     * @return Event Service
     */
    public static EventService getEventService(Interceptor interceptor) {
        return getRetrofitInstance(interceptor).baseUrl(ServiceUtil.TPLANNER_BASE_URL).build().create(EventService.class);
    }

    /**
     * Get Category Service API.
     *
     * @return Category Service
     */
    public static CategoryService getCategoryService(Interceptor interceptor){
        return getRetrofitInstance(interceptor).baseUrl(ServiceUtil.TPLANNER_BASE_URL).build().create(CategoryService.class);
    }

    /**
     * Get Festival Service API.
     *
     * @return Festival Service
     */
    public static FestivalService getFestivalService(Interceptor interceptor){
        return getRetrofitInstance(interceptor).baseUrl(ServiceUtil.TPLANNER_BASE_URL).build().create(FestivalService.class);
    }


    /**
     * Insert new services here!
     */
}