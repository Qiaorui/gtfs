package network.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by qiaoruixiang on 13/08/2017.
 */

public class CategoryScoreResponse {
    @SerializedName("categoryID")
    private Long categoryID;

    @SerializedName("score")
    private int score;

    public CategoryScoreResponse(Long categoryID, int score) {
        this.categoryID = categoryID;
        this.score = score;
    }

    public Long getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Long categoryID) {
        this.categoryID = categoryID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
