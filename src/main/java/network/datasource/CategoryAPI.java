package network.datasource;

import network.model.response.CategoryResponse;

import java.io.IOException;
import java.util.List;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public interface CategoryAPI {

    void getAllCategoriesByFestival(GetAllCategoriesCallback callback, Long festivalID, String lang);
    List<CategoryResponse> getAllCategoriesByFestival(Long festivalID, String lang) throws IOException;


    interface GetAllCategoriesCallback {
        void onCategoriesRetrieved(List<CategoryResponse> categoriesResponse);
        void onCategoriesRetrievedError(int errorCode);
    }
}
