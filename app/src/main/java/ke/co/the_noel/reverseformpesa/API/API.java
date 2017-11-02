package ke.co.the_noel.reverseformpesa.API;

import ke.co.the_noel.reverseformpesa.models.AuthResponseModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by admin on 01/11/2017.
 */

public interface API {
    @GET("generate?grant_type=client_credentials")
    Call<AuthResponseModel> authenticate();

}
