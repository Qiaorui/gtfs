package network.datasource.impl;

import network.datasource.CategoryAPI;
import network.model.response.CategoryResponse;
import network.service.CategoryService;
import network.utils.RetrofitClient;
import utils.Const;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public class CategoryAPIImpl implements CategoryAPI {

    private static final String TAG = "CATEGORY_API";

    private CategoryService mCategoryService;
    private GetAllCategoriesCallback mGetCategoriesCallback;


    public CategoryAPIImpl() {
        mCategoryService = RetrofitClient.getCategoryService(new Interceptor() {
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
    public void getAllCategoriesByFestival(GetAllCategoriesCallback callback, Long festivalID, String lang) {
        mGetCategoriesCallback = callback;
        Call<List<CategoryResponse>> call = mCategoryService.getCategories(festivalID, lang);
        call.enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, retrofit2.Response<List<CategoryResponse>> response) {
                List<CategoryResponse> cats = response.body();
                System.out.println("Retrieved " + cats.size() + " categories");
                mGetCategoriesCallback.onCategoriesRetrieved(cats);
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                System.out.println(t.toString());
                if (t instanceof SocketTimeoutException) {
                    mGetCategoriesCallback.onCategoriesRetrievedError(Const.TIME_OUT_ERROR);
                }
                if (t instanceof SocketException) {
                    mGetCategoriesCallback.onCategoriesRetrievedError(Const.NETWORK_UNREACHABLE_ERROR);
                }
            }
        });
    }

    @Override
    public List<CategoryResponse> getAllCategoriesByFestival(Long festivalID, String lang) throws IOException {
        Call<List<CategoryResponse>> call = mCategoryService.getCategories(festivalID, lang);
        return call.execute().body();
    }
}
