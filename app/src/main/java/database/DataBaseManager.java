package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by wilbert on 2016/12/1.
 */
public class DataBaseManager {
    private static volatile DataBaseManager dataBaseManager = null;
    private Context context;
    private SQLiteDatabase database;
    private DataBaseManager(Context context) {
        this.context = context.getApplicationContext();
        DataBaseHelper helper = new DataBaseHelper(context, "TravelAlbum.db", null, 2);
        database = helper.getWritableDatabase();
    }


    public static DataBaseManager getDataBaseManager(Context context) {
        if (dataBaseManager == null) {
            synchronized (DataBaseManager.class) {
                dataBaseManager = new DataBaseManager(context);
            }
        }
        return dataBaseManager;
    }

    public void execSQL(String sql) {
        database.execSQL(sql);
        return;
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return database.rawQuery(sql, selectionArgs);
    }
}
