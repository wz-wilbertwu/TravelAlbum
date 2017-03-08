package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import util.LogUti;

/**
 * Created by wilbert on 2016/11/25.
 */
public class Travel implements Parcelable{
    private String user_id;

    public String getId() {
        return id;
    }

    private String id;

    protected Travel(Parcel in) {
        user_id = in.readString();
        id = in.readString();
        title = in.readString();
        time = in.readString();
    }

    public static final Creator<Travel> CREATOR = new Creator<Travel>() {
        @Override
        public Travel createFromParcel(Parcel in) {
            return new Travel(in);
        }

        @Override
        public Travel[] newArray(int size) {
            return new Travel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    private String title;
    private String time;
    public Travel(String user_id, String title) {
        this.user_id = user_id;
        this.title = title;
        id = UUID.randomUUID().toString().replaceAll("-", "");
        time = DateFormat.getDateTimeInstance().format(new Date());
    }

    public Travel(String id, String user_id, String title, String time) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.time = time;
    }
    public  String getTravelInsertSql() {
        String sql = "INSERT INTO tb_travel (id, user_id, time, title) " +
                "VALUES (\"%s\", \"%s\", \"%s\", \"%s\")";
        String result =  String.format(sql, id, user_id, time, title);
        LogUti.d("insert travel:" + result);
        return result;
    }

    public String getTravelRemoveSql() {
        String sql = "delete from tb_travel where id = '%s'";
        String result =  String.format(sql, id);
        LogUti.d("remove travel:" + result);
        return result;
    }

    public String getTravelUpdateSql() {
        String sql = "update tb_travel SET title = '%s', time = '%s' " +
                "where id = '%s'";
        String result =  String.format(sql, title, time, id);
        LogUti.d("update travel:" + result);
        return result;
    }

    public static String getTravelQuery(String id) {
        String sql = "select * from tb_travel where id = '%s'";
        String result =  String.format(sql, id);
        LogUti.d("query travel:" + result);
        return result;
    }

    public static String getTravelQueryAll(String user_id) {
        String sql = "select * from tb_travel where user_id = '%s'";
        String result =  String.format(sql, user_id);
        LogUti.d("query all travel:" + result);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(time);
    }
}
