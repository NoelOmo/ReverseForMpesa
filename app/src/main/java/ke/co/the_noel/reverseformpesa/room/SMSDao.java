package ke.co.the_noel.reverseformpesa.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;


/**
 * Created by HP on 12/3/2017.
 */

@Dao
public interface SMSDao {

    @Query("SELECT * FROM sms ORDER BY sms_time")
    List<SMS> getAll();

    @Query("SELECT * FROM sms ORDER BY sms_time DESC LIMIT 1")
    SMS getLatest();

    @Insert
    void insert(SMS sms);

    @Insert
    void nsertAll(List<SMS> smses);



}
