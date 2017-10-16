package network.datasource.impl;

import network.datasource.EventAPI;
import network.model.response.EventResponse;
import network.service.EventService;
import network.utils.RetrofitClient;
import utils.Const;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class EventAPIImpl implements EventAPI {

    private static final String TAG = "EVENT_API";

    private EventService mEventService;
    private GetEventsCallback mGetEventsCallback;

    public EventAPIImpl() {
        mEventService = RetrofitClient.getEventService(new Interceptor() {
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
    public void getAllEventByLastDate(GetEventsCallback callback, Long festivalID, long timestamp, String lang) {
        mGetEventsCallback = callback;
        Call<List<EventResponse>> call = mEventService.getEvents(festivalID, timestamp, lang);
        call.enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, retrofit2.Response<List<EventResponse>> response) {
                List<EventResponse> events = (response.body() == null) ? new ArrayList<EventResponse>() : response.body();
                System.out.println("Retrieved " + events.size() + " events");
                mGetEventsCallback.onEventsRetrieved(events);
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                System.out.println(t.toString());
                if (t instanceof SocketTimeoutException) {
                    mGetEventsCallback.onEventsRetrievedError(Const.TIME_OUT_ERROR);
                }
                if (t instanceof SocketException) {
                    mGetEventsCallback.onEventsRetrievedError(Const.NETWORK_UNREACHABLE_ERROR);
                }
            }
        });
    }

    @Override
    public List<EventResponse> getAllEventByLastDate(Long festivalID, long timestamp, String lang) throws IOException {
        Call<List<EventResponse>> call = mEventService.getEvents(festivalID, timestamp, lang);
        return call.execute().body();
    }
}
