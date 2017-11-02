package ke.co.the_noel.reverseformpesa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import ke.co.the_noel.reverseformpesa.API.API;
import ke.co.the_noel.reverseformpesa.API.APIContract;
import ke.co.the_noel.reverseformpesa.API.RetrofitBuilder;
import ke.co.the_noel.reverseformpesa.models.AuthResponseModel;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    RetrofitBuilder retrofitBuilder;
    API retro;

    private static MainActivity inst;

    public static MainActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getNewToken();

    }

    public void getNewToken(){

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Request request = chain.request().newBuilder()
                        .addHeader("authorization", "Basic " + getBase64(APIContract.CONSUMER_KEY, APIContract.CONSUMER_SECRET))
                        .addHeader("cache-control", "no-cache")
                        .build();
                return chain.proceed(request);
            }
        });
        httpClient.addInterceptor(logging);

        retrofitBuilder = new RetrofitBuilder(httpClient);
        Retrofit retrofit = retrofitBuilder.getRetrofitClient();

        retro = retrofit.create(API.class);

        Call<AuthResponseModel> call = retro.authenticate();
        call.enqueue(new Callback<AuthResponseModel>() {
            @Override
            public void onResponse(Call<AuthResponseModel> call, retrofit2.Response<AuthResponseModel> response) {
                if (response.isSuccessful())
                    Log.i("ACCESS_TOKEN", response.body().accessToken);
                else
                    Log.i("ACCESS_TOKEN", "It failed");
            }

            @Override
            public void onFailure(Call<AuthResponseModel> call, Throwable t) {
                Log.i("FAILED", t.getLocalizedMessage());
                t.printStackTrace();

            }
        });
    }


    public void handleMessage(String smsBody, String address) {

        int i = smsBody.indexOf(' ');
        String code = smsBody.substring(0, i);
        if (address.equals("MPESA")){
            Log.i("MESSAGE", smsBody);
            Log.i("CODE", code);
            Log.i("ADDRESS", address);
        }
    }


    public String getBase64(String app_key, String app_secret) throws UnsupportedEncodingException {
        String appKeySecret = app_key + ":" + app_secret;
        byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
        String auth = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return auth;
    }



}
