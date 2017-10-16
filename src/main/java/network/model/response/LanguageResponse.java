package network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public class LanguageResponse {

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    public LanguageResponse(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
