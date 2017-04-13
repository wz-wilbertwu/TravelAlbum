package database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import model.Travel;

/**
 * Created by wilbert on 2017/3/23.
 */
public class SyncHelper {
    private SQLiteDatabase database;
    private Context context;
    private RequestQueue requestQueue;
    private List uploadTravelListFromClient(Context context, String userId) {
        DataBaseHelper helper = new DataBaseHelper(context, "TravelAlbum.db", null, 2);
        database = helper.getWritableDatabase();
        List<Travel> travelList = new ArrayList();
        this.context = context;
        String sql = String.format
                ("select * from tb_travel where user_id = '%s' and state != '9'", userId);
        Cursor cursor = database.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String user_id = cursor.getInt(cursor.getColumnIndex("user_id")) + "";
            String time = cursor.getInt(cursor.getColumnIndex("time")) + "";
            String title = cursor.getString(cursor.getColumnIndex("title"));
            Travel travel = new Travel(id,user_id, title, time);
            travelList.add(travel);
        }
        requestQueue = Volley.newRequestQueue(context);

        return travelList;
    }
}
