package ke.co.the_noel.reverseformpesa.API;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 01/11/2017.
 */

public class RetrofitBuilder {

    OkHttpClient.Builder httpClient;

    public RetrofitBuilder(OkHttpClient.Builder httpClient) {
        this.httpClient = httpClient;

    }

    Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public Retrofit getRetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIContract.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();
        return retrofit;
    }
}
