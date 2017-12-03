package ke.co.the_noel.reverseformpesa.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;


/**
 * Created by HP on 12/3/2017.
 */

@Entity(tableName = "sms")
public class SMS {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "sms_body")
    public String smsBody;

    @ColumnInfo(name = "sms_time")
    public String smsTime;

    public int getId() {
        return id;
    }

    public String getSmsBody() {
        return smsBody;
    }

    public String getSmsTime() {
        return smsTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public void setSmsTime(String smsTime) {
        this.smsTime = smsTime;
    }
}
