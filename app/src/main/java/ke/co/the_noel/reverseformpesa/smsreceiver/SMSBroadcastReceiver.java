package ke.co.the_noel.reverseformpesa.smsreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import ke.co.the_noel.reverseformpesa.MainActivity;

public class SMSBroadcastReceiver extends BroadcastReceiver {

    public static final String SMS_BUNDLE = "pdus";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            String smsBody = "", address = "";
            for (int i = 0; i < sms.length; ++i) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);

                smsBody = smsMessage.getMessageBody().toString();
                address = smsMessage.getOriginatingAddress();
                Log.i("SMS ADDRESS", address);

            }

            //this will update the UI with message
            if(address.equals("MPESA") || address.equals("+16505556789")) {
                MainActivity inst = MainActivity.instance();
                inst.handleMessage(smsBody, address);
            }
        }
    }
}
