package model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private String operation;

    protected Travel(Parcel in) {
        user_id = in.readString();
        id = in.readString();
        title = in.readString();
        time = in.readString();
        status = in.readString();
        state = in.readString();
        operation = in.readString();
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    private String title;
    private String time;
    private String status;//status 指的是网络状态的succ或者fail
    private String state;
    //state 数据的当前状态
    // 0：本地新增
    // -1：标记删除
    // 1：本地更新
    // 9：已经同步

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Travel(String user_id, String title) {
        this.user_id = user_id;
        this.title = title;
        id = UUID.randomUUID().toString().replaceAll("-", "");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = format.format(new Date());
        state = "0";
    }

    public Travel(String id, String user_id, String title, String time) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.time = time;
        this.state = "0";
    }

    public Travel(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            id = jsonObject.getString("id");
            user_id = jsonObject.getString("userId");
            title = jsonObject.getString("title");
            time = jsonObject.getString("time");
            status = jsonObject.getString("status");
            operation = jsonObject.getString("operation");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public  String getTravelInsertSql() {
        state = "0";
        String sql = "INSERT INTO tb_travel (id, user_id, time, title, state) " +
                "VALUES (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\")";
        String result =  String.format(sql, id, user_id, time, title, state);
        LogUti.d("insert travel:" + result);
        return result;
    }

    public String getTravelMarkRemoveSql() {
        state = "-1";
        /*String sql = "delete from tb_travel where id = '%s'";
        String result =  String.format(sql, id);*/
        String sql = "update tb_travel SET state = '%s'" +
                "where id = '%s'";
        String result =  String.format(sql, state, id);
        LogUti.d("remove travel:" + result);
        return result;
    }

    public String getTravelRemoveSql() {
        state = "-1";
        String sql = "delete from tb_travel where id = '%s'";
        String result =  String.format(sql, id);
        LogUti.d("remove travel:" + result);
        return result;
    }

    public String getTravelUpdateSql() {
        state = "1";
        String sql = "update tb_travel SET title = '%s', time = '%s', state = '%s'" +
                "where id = '%s'";
        String result =  String.format(sql, title, time, state, id);
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

    public String getTravelUpdateStateSql() {
        state = "9";
        String sql = "update tb_travel SET state = '%s'" +
                "where id = '%s'";
        String result =  String.format(sql, state, id);
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
        dest.writeString(status);
        dest.writeString(state);
        dest.writeString(operation);
    }

    public Map getMap() {
        Map map = new HashMap<String, String>();
        map.put("userId", user_id == null ? "":user_id);
        map.put("id", id == null ? "":id);
        map.put("title", title == null ? "":title);
        map.put("time", time == null ? "":time);
        return map;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
