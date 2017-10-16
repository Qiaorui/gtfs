package network.datasource.impl;


import network.datasource.FestivalAPI;
import network.model.response.FestivalResponse;
import network.service.FestivalService;
import network.utils.RetrofitClient;
import utils.Const;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class FestivalAPIImpl implements FestivalAPI {

    private static final String TAG = "FESTIVAL_API";

    private FestivalService mFestivalService;
    private GetFestivalCallback mGetFestivalCallback;

    public FestivalAPIImpl() {
        mFestivalService = RetrofitClient.getFestivalService(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                return onIntercept(chain);
            }
        });
    }

    private Response onIntercept(Interceptor.Chain chain) throws IOException {
        try {
            Response response = chain.proceed(chain.request());
            return response;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        }

    }


    @Override
    public void getFestivalDetail(GetFestivalCallback callback, Long festivalID, String lang) {
        mGetFestivalCallback = callback;
        Call<FestivalResponse> call = mFestivalService.getFestival(festivalID, lang);
        call.enqueue(new Callback<FestivalResponse>() {
            @Override
            public void onResponse(Call<FestivalResponse> call, retrofit2.Response<FestivalResponse> response) {
                FestivalResponse f = response.body();
                System.out.println("Retrieved " + f.getName());
                mGetFestivalCallback.onFestivalRetrieved(f);
            }

            @Override
            public void onFailure(Call<FestivalResponse> call, Throwable t) {
                System.out.println(t.toString());
                if (t instanceof SocketTimeoutException) {
                    mGetFestivalCallback.onFestivalRetrievedError(Const.TIME_OUT_ERROR);
                }
                if (t instanceof SocketException) {
                    mGetFestivalCallback.onFestivalRetrievedError(Const.NETWORK_UNREACHABLE_ERROR);
                }
            }
        });
    }

    @Override
    public FestivalResponse getFestivalDetail(Long festivalID, String lang) throws IOException {
        Call<FestivalResponse> call = mFestivalService.getFestival(festivalID, lang);
        return call.execute().body();
    }
}
