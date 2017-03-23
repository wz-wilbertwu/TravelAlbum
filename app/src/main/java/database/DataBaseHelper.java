package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import util.LogUti;

/**
 * Created by wilbert on 2016/11/25.
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_TRAVEL = "CREATE TABLE tb_travel (" +
            "id  TEXT(36)," +
            "user_id  TEXT(36) NOT NULL," +
            "time  TEXT(100)," +
            "title  TEXT(255) NOT NULL," +
            "state TEXT(10) NOT NULL" +
            ")";
    public static final String CREATE_TRAVEL_ITEM = "CREATE TABLE tb_travel_item (" +
            "id  TEXT(36) NOT NULL," +
            "travel_id  TEXT(36)," +
            "description  TEXT(100)," +
            "time  TEXT(255)," +
            "title  TEXT(100)," +
            "image  TEXT(255)," +
            "state TEXT(10) NOT NULL," +
            "PRIMARY KEY (id))";
    private Context context;

    public DataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TRAVEL);
        LogUti.d("create travel successfully");
        db.execSQL(CREATE_TRAVEL_ITEM);
        LogUti.d("create travel item successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
