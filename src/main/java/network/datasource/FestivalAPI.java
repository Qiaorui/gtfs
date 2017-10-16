package network.datasource;

import network.model.response.FestivalResponse;

import java.io.IOException;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public interface FestivalAPI {

    void getFestivalDetail(GetFestivalCallback callback, Long festivalID, String lang);

    FestivalResponse getFestivalDetail(Long festivalID, String lang) throws IOException;


    interface GetFestivalCallback {
        void onFestivalRetrieved(FestivalResponse festivalResponse);
        void onFestivalRetrievedError(int errorCode);
    }

}
