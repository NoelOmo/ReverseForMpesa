package ke.co.the_noel.reverseformpesa;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;


import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
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

public class MainActivity extends AppCompatActivity implements ReverseConfirmationDialog.ReverseIt {

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

    //Constants
    int PERMISSION_REQUEST_READ_SMS = 9799;

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

        requestForSMSPermission();


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
        reverseIntent.putExtra("ReverseClicked", true);
        piReverse = PendingIntent.getService(this, 0, reverseIntent, 0);

        mBuilder =  new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher_background)
        .setStyle(new NotificationCompat.BigTextStyle());

        mNotifyMgr =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);





    }

    private void requestForSMSPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
            if (!checkForSMSPermission()){

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_SMS},
                        PERMISSION_REQUEST_READ_SMS);
            }
        }
    }

    private boolean checkForSMSPermission() {
        int smsRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        if (smsRead == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
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
        if (address.equals("MPESA") || address.equals("+16505556789")){
            this.smsBody = smsBody;
            Log.i("MESSAGE", smsBody);
            Log.i("CODE", code);
            Log.i("ADDRESS", address);
            mBuilder.setContentTitle(code);
            mBuilder.setContentText(smsBody);
            mBuilder.setStyle(new NotificationCompat.BigTextStyle()
            .bigText(smsBody))
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

    public void showLoadingDialog(){
        Animation bottomUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bottom_up);
        ViewGroup hiddenPanel = findViewById(R.id.hidden_panel);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
    }

    @Override
    public void handleReverseButtonClick() {
        showLoadingDialog();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_READ_SMS){

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }else {
                showPermissionDeniedDialog();
            }

            }


    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Permission required")
                .setMessage("This app requires permission to access your MPESA text messages in order to function properly. Reverse for Mpesa will now exit as the permission was not granted.")
                .setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       System.exit(1);
                    }
                })
                .setNegativeButton("GRANT ACCESS", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requestForSMSPermission();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
