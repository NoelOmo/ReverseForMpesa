package ke.co.the_noel.reverseformpesa.database;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomDatabase;

import ke.co.the_noel.reverseformpesa.room.SMS;
import ke.co.the_noel.reverseformpesa.room.SMSDao;

/**
 * Created by HP on 12/3/2017.
 */

@Database(entities = {SMS.class}, version = 1)
public abstract class SMSDatabase extends RoomDatabase {

    public abstract SMSDao smsDao();

    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }
}
