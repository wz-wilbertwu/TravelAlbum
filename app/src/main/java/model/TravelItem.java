package model;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import util.LogUti;

/**
 * Created by wilbert on 2016/11/29.
 */
public class TravelItem {
    private String id;
    private String travel_id;
    private String description;
    private String image;
    private String time;
    public TravelItem(String travel_id, String description, String image) {
        this.travel_id = travel_id;
        this.description = description;
        this.image = image;
        id = UUID.randomUUID().toString().replaceAll("-", "");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = format.format(new Date());
    }
    public TravelItem(String id, String travel_id, String description, String image, String time) {
        this.travel_id = travel_id;
        this.description = description;
        this.image = image;
        this.id = id;
        this.time = time;
    }

    public String getTravelItemInsertSql() {
        String sql = "INSERT INTO tb_travel_item (id, travel_id, description, time) " +
                "VALUES ('%s', '%s', '%s', '%s')";
        String result = String.format(sql, id, travel_id, description, time);
        LogUti.d("insert travel item:" + result);
        return result;
    }

    public static String getTravelItemQueryAllSql(String travel_id) {
        String sql = "select * from tb_travel_item where travel_id = '%s'";
        String result = String.format(sql, travel_id);
        LogUti.d("query all travel item:" + result);
        return result;
    }

    @Override
    public String toString() {
        String result = String.format("travel item:\nid:%s\ntravel_id:%s\nimage:%s\ndescription:%s\n",
                                        id, travel_id, image, description);
        return result;
    }
}
