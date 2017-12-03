package ke.co.the_noel.reverseformpesa.models;

import java.util.Date;

/**
 * Created by HP on 12/3/2017.
 */

public class SMSModel {

    String smsBody;
    Date smsDate;

    public SMSModel(String smsBody, Date smsDate) {
        this.smsBody = smsBody;
        this.smsDate = smsDate;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public Date getSmsDate() {
        return smsDate;
    }
}
