package ke.co.the_noel.reverseformpesa;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

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

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyMgr;
    int mNotificationId = 001;
    PendingIntent piDismiss;
    PendingIntent piReverse;

    String securityCert;
    AssetManager assetManager;
    Toolbar toolbar;
    String smsBody;

    Button btnReverse;

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
        btnReverse = findViewById(R.id.btn_reverse);

        btnReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReverseDialog();
            }
        });

        securityCert = "file:///android_asset/cert.cer";
        assetManager = getAssets();
        try {
            AssetFileDescriptor assetFileDescriptor = this.getAssets().openFd("cert.cer");
            FileDescriptor fileDescriptor = assetFileDescriptor.getFileDescriptor();
            encryptInitiatorPassword(fileDescriptor, "1234");

        } catch (IOException e) {
            e.printStackTrace();
        }

        getNewToken();

        Intent dismissIntent = new Intent(this, MainActivity.class);
        dismissIntent.setAction("Dismiss");
        piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

        Intent reverseIntent = new Intent(this, MainActivity.class);
        reverseIntent.setAction("Reverse");
        piReverse = PendingIntent.getService(this, 0, reverseIntent, 0);

        mBuilder =  new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
        .setStyle(new NotificationCompat.BigTextStyle());

        mNotifyMgr =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);





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
            this.smsBody = smsBody;
            Log.i("MESSAGE", smsBody);
            Log.i("CODE", code);
            Log.i("ADDRESS", address);
            mBuilder.setContentTitle(code);
            mBuilder.setContentText("You have just paid Ksh1,000 to SOME ORG, for account 178505" );
            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
            .bigText("You have just paid Ksh1,000 to SOME ORG, for account 178505"))
                    .addAction (R.drawable.ic_launcher_background,  "Dismiss", piDismiss)
                    .addAction (R.drawable.ic_launcher_background, "Reverse", piReverse);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }


    public String getBase64(String app_key, String app_secret) throws UnsupportedEncodingException {
        String appKeySecret = app_key + ":" + app_secret;
        byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
        String auth = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return auth;
    }


    // Function to encrypt the initiator credentials
    public String encryptInitiatorPassword(FileDescriptor securityCertificate, String password) {

        String encryptedPassword = "YOUR_INITIATOR_PASSWORD";
        try {
            Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
            byte[] input = password.getBytes();

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
            FileInputStream fin = new FileInputStream(securityCertificate);
            InputStream inputStream = assetManager.open("cert.cer");
            CertificateFactory cf = CertificateFactory.getInstance("X.509", "BC");
            X509Certificate certificate = (X509Certificate) cf.generateCertificate(inputStream);
            PublicKey pk = certificate.getPublicKey();
            cipher.init(Cipher.ENCRYPT_MODE, pk);

            byte[] cipherText = cipher.doFinal(input);

            // Convert the resulting encrypted byte array into a string using base64 encoding
            encryptedPassword = Base64.encodeToString(cipherText, Base64.NO_WRAP);

        } catch (NoSuchAlgorithmException ex) {
            //Logger.getLogger(PasswordUtil.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (NoSuchProviderException ex) {
            //Logger.getLogger(PasswordUtil.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (NoSuchPaddingException ex) {
            //Logger.getLogger(PasswordUtil.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (CertificateException ex) {
            //Logger.getLogger(PasswordUtil.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (InvalidKeyException ex) {
            //Logger.getLogger(PasswordUtil.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (IllegalBlockSizeException ex) {
            //Logger.getLogger(PasswordUtil.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (BadPaddingException ex) {
            //Logger.getLogger(PasswordUtil.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("ENCRYPTED PASSWORD", encryptedPassword + "SUCCESS");

        return encryptedPassword;
    }

    public void showReverseDialog(){
        FragmentManager fm = getSupportFragmentManager();
        Bundle b = new Bundle();
        b.putString("text_msg", smsBody);
        ReverseConfirmationDialog dialogFragment = new ReverseConfirmationDialog();

        dialogFragment.setArguments(b);
        dialogFragment.show(fm, "Login failed");

    }

}
