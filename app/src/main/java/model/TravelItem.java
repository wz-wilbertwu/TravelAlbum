package model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import util.LogUti;

/**
 * Created by wilbert on 2016/11/29.
 */
public class TravelItem implements Parcelable{
    private String id;
    private String travel_id;

    public String getImage() {
        return image;
    }

    public String getTime() {
        return time;
    }

    public String getDescription() {
        return description;
    }

    public String getTravel_id() {
        return travel_id;
    }

    public String getId() {
        return id;
    }

    private String description;
    private String image;
    private String time;
    private String status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TravelItem(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            id = jsonObject.getString("id");
            travel_id = jsonObject.getString("travel_id");
            description = jsonObject.getString("description");
            image = jsonObject.getString("image");
            time = jsonObject.getString("time");
            status = jsonObject.getString("status");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public String getTravelItemInsertSql() {
        String sql = "INSERT INTO tb_travel_item (id, travel_id, description, time, image) " +
                "VALUES ('%s', '%s', '%s', '%s', '%s')";
        String result = String.format(sql, id, travel_id, description, time, image);
        LogUti.d("insert travel item:" + result);
        return result;
    }

    public String getTravelItemRemoveSql() {
        String sql = "delete from tb_travel_item where id = '%s'";
        String result =  String.format(sql, id);
        LogUti.d("remove travel item:" + result);
        return result;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTravelItemUpdateSql() {
        String sql = "update tb_travel_item SET description = '%s', image = '%s', time = '%s' " +
                "where id = '%s'";
        String result =  String.format(sql, description, image, time, id);
        LogUti.d("update travel:" + result);
        return result;
    }

    public Map getMap() {
        Map map = new HashMap<String, String>();
        map.put("id", id == null ? "" : id);
        map.put("travel_id", travel_id == null ? "":travel_id);
        map.put("description", description == null ? "":description);
        map.put("image", image == null ? "":image);
        map.put("time", time == null ? "":time);
        return map;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(travel_id);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(time);
        dest.writeString(status);
    }
    protected TravelItem(Parcel in) {
        id = in.readString();
        travel_id = in.readString();
        description = in.readString();
        image = in.readString();
        time = in.readString();
        status = in.readString();
    }
    public static final Creator<TravelItem> CREATOR = new Creator<TravelItem>() {
        @Override
        public TravelItem createFromParcel(Parcel in) {
            return new TravelItem(in);
        }

        @Override
        public TravelItem[] newArray(int size) {
            return new TravelItem[size];
        }
    };
}
