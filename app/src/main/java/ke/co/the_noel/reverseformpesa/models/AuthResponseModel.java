package ke.co.the_noel.reverseformpesa.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by admin on 01/11/2017.
 */

public class AuthResponseModel {
    @SerializedName("access_token")
    @Expose
    public String accessToken;
    @SerializedName("expires_in")
    @Expose
    public String expiresIn;
}
