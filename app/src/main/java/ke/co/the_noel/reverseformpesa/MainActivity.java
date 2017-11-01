package ke.co.the_noel.reverseformpesa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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


}
